package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.CartItemResponse;
import com.example.Shoe_shop.dto.response.OrderResponse;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.entity.*;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.OrderMapper;
import com.example.Shoe_shop.repository.*;
import com.example.Shoe_shop.service.OrderService;
import com.example.Shoe_shop.utils.CheckRole;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import com.example.Shoe_shop.utils.TrackingNumberUtil;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    UserRepository userRepository;
    OrderRepository orderRepository;
    CartJdbcRepository cartJdbcRepository;
    PurchaseOrderItemRepository purchaseOrderItemRepository;
    EntityValidatorUtil entityValidatorUtil;
    TrackingNumberUtil  trackingNumberUtil;
    OrderMapper orderMapper;


    @Override
    @Transactional
    public OrderResponse createOrderFromCart(Long userId,OrderRequest orderRequest) {
        User user=entityValidatorUtil.requireUser(userId);
        Long cartId= cartJdbcRepository.getCartIdByUserId(user.getId());
        List<CartItemResponse> cartItems=cartJdbcRepository.findCartItems(cartId);
        //Lọc theo cartItems nếu có
        if (orderRequest.getCartItemIds() != null && !orderRequest.getCartItemIds().isEmpty()) {
            cartItems=cartItems.stream()
                    .filter(cartItemResponse -> orderRequest.getCartItemIds().contains(cartItemResponse.getCartItemId()))
                    .toList();
        }
        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        ShippingMethod shippingMethod=entityValidatorUtil.requireShipping(orderRequest.getShippingMethod().getId());

        Order order=orderMapper.toEntity(orderRequest);
        order.setUser(user);
        order.setShippingMethod(shippingMethod);
        order.setTrackingNumber(trackingNumberUtil.generateUnique());
        order.setShippingFee(shippingMethod.getFee());
        order.setOrderDetails(new ArrayList<>());

        BigDecimal totalAmount=BigDecimal.ZERO;
        BigDecimal totalCost=BigDecimal.ZERO;
        for (CartItemResponse item : cartItems) {
            totalAmount=totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));

            ProductVariant variant=entityValidatorUtil.requireProductVariant(item.getVariantId());

            int quantityToSell=item.getQuantity();
            List<PurchaseOrderItem> fifoItems=purchaseOrderItemRepository.findAllByVariantId(item.getVariantId(),0);

            if (fifoItems.isEmpty()){
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            for (PurchaseOrderItem poItem : fifoItems) {
                if (quantityToSell <= 0) break;

                int sellQty = Math.min(quantityToSell, poItem.getRemainingQty());
                totalCost = totalCost.add(poItem.getUnitCost().multiply(BigDecimal.valueOf(sellQty)));

                // Tạo OrderDetail
                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .variant(variant)
                        .quantity(sellQty)
                        .unitPrice(item.getUnitPrice())
                        .unitCost(poItem.getUnitCost())
                        .build();
                order.getOrderDetails().add(detail);

                // Cập nhật remainingQty
                poItem.setRemainingQty(poItem.getRemainingQty() - sellQty);
                purchaseOrderItemRepository.save(poItem);

                quantityToSell -= sellQty;
            }
            if (quantityToSell > 0) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
            // Xoá item khỏi cart
            cartJdbcRepository.removeItem(cartId, item.getVariantId());
        }

        order.setTotalAmount(totalAmount);
        order.setTotalCost(totalCost);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(Long userId) {
        if(!userRepository.existsById(userId)){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return orderRepository.findOrderByUserId(userId)
                .stream()
                .map(orderSummaryProjection -> new OrderResponse(
                        orderSummaryProjection.getId(),
                        orderSummaryProjection.getTotalAmount(),
                        orderSummaryProjection.getStatus(),
                        orderSummaryProjection.getCreatedAt()
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrder(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.fromString(sortDir), sortBy));
       Page<Order> ordersPage=orderRepository.findAll(pageable);
       List<OrderResponse> content=ordersPage.stream()
               .map(orderMapper::toResponse)
               .toList();
        return new PagedResponse<>(
                ordersPage.getNumber(),
                ordersPage.getSize(),
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                content
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getOrdersbyStatus(Long userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders;
        if(CheckRole.isAdmin()){
            orders=orderRepository.findAllByStatus(status, pageable);
        }
        else {
            orders=orderRepository.findAllByUserIdAndStatus(userId, status, pageable);
        }
        List<OrderResponse> content=orders.getContent().stream()
                .map(orderMapper::toResponse)
                .toList();
        return new PagedResponse<>(
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                content
        );
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order=entityValidatorUtil.requireOrder(id);
        if(!isValidStatusTransition(order.getStatus(),newStatus)){
            throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order=entityValidatorUtil.requireOrder(orderId);

        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();
        if (CheckRole.isAdmin()) {
            order.setStatus(OrderStatus.CANCELLED);
        } else {
            if (!order.getUser().getUsername().equals(username)) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new AppException(ErrorCode.ORDER_STATUS_IS_NOT_PENDING);
            }
            order.setStatus(OrderStatus.CANCELLED);
        }
        orderRepository.save(order);
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {
        switch(current) {
            case PENDING: return next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
            case PROCESSING: return next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED: return next == OrderStatus.DELIVERED;
            default: return false;
        }
    }

}
