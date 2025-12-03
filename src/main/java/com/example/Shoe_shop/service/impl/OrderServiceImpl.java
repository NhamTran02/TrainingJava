package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.request.PaymentRequest;
import com.example.Shoe_shop.dto.response.*;
import com.example.Shoe_shop.entity.*;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.OrderMapper;
import com.example.Shoe_shop.repository.*;
import com.example.Shoe_shop.service.EmailService;
import com.example.Shoe_shop.service.OrderService;
import com.example.Shoe_shop.service.PaymentService;
import com.example.Shoe_shop.utils.CheckRole;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import com.example.Shoe_shop.utils.TrackingNumberUtil;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import com.example.Shoe_shop.utils.enums.PaymentMethod;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import com.example.Shoe_shop.utils.enums.ShippingType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class OrderServiceImpl implements OrderService {
    final UserRepository userRepository;
    final OrderRepository orderRepository;
    final CartJdbcRepository cartJdbcRepository;
    final PurchaseOrderItemRepository purchaseOrderItemRepository;
    final EntityValidatorUtil entityValidatorUtil;
    final TrackingNumberUtil  trackingNumberUtil;
    final OrderMapper orderMapper;
    final PaymentService paymentService;
    final EmailService emailService;
    final ShippingMethodRepository shippingMethodRepository;

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(Long userId, OrderRequest orderRequest, HttpServletRequest request) {
        User user = entityValidatorUtil.requireUser(userId);
        Long cartId = cartJdbcRepository.getCartIdByUserId(user.getId());

        List<CartItemResponse> cartItems = cartJdbcRepository.findCartItems(cartId)
                .stream()
                .filter(CartItemResponse::getSelected)
                .toList();

        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        ShippingType shippingType= orderRequest.getShippingType();
        BigDecimal shippingFee= shippingType.getFee();
        ShippingMethod shippingMethod = shippingMethodRepository.findByMethodName(shippingType)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_METHOD_NOT_FOUND));
        String trackingNumber = trackingNumberUtil.generateUnique();

        Order order = orderMapper.toEntity(orderRequest);
        order.setUser(user);
        order.setShippingMethod(shippingMethod);
        order.setTrackingNumber(trackingNumber);
        order.setShippingFee(shippingFee);
        order.setOrderDetails(new ArrayList<>());

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (CartItemResponse item : cartItems) {
            totalAmount = totalAmount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            ProductVariant variant = entityValidatorUtil.requireProductVariant(item.getVariantResponse().getId());

            int quantityToSell = item.getQuantity();
            List<PurchaseOrderItem> fifoItems = purchaseOrderItemRepository.findAllByVariantId(item.getVariantResponse().getId(), 0);

            if (fifoItems.isEmpty()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            for (PurchaseOrderItem poItem : fifoItems) {
                if (quantityToSell <= 0) break;

                int sellQty = Math.min(quantityToSell, poItem.getRemainingQty());
                totalCost = totalCost.add(poItem.getUnitCost().multiply(BigDecimal.valueOf(sellQty)));

                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .variant(variant)
                        .quantity(sellQty)
                        .unitPrice(item.getUnitPrice())
                        .unitCost(poItem.getUnitCost())
                        .build();
                order.getOrderDetails().add(detail);

                poItem.setRemainingQty(poItem.getRemainingQty() - sellQty);
                purchaseOrderItemRepository.save(poItem);

                quantityToSell -= sellQty;
            }
            if (quantityToSell > 0) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            cartJdbcRepository.removeItem(cartId, item.getVariantResponse().getId());
        }

        order.setTotalAmount(totalAmount.add(order.getShippingFee()));
        order.setTotalCost(totalCost);
        Order savedOrder = orderRepository.save(order);
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .amount(savedOrder.getTotalAmount())
                .paymentMethod(orderRequest.getPaymentMethod())
                .note(orderRequest.getNote())
                .build();

        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        OrderResponse response = orderMapper.toResponse(savedOrder);
        if (orderRequest.getPaymentMethod() == PaymentMethod.VNPAY) {
            String payUrl = paymentService.createVnpayPaymentUrl(paymentResponse, request);
            response.setPaymentUrl(payUrl);
        }
        response.setTrackingNumber(trackingNumber);
        emailService.sendOrderConfirmation(order);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
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
                        orderSummaryProjection.getCreatedAt(),
                        orderSummaryProjection.getShippingFee(),
                        orderSummaryProjection.getTrackingNumber(),
                        null
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
    @Transactional(rollbackFor = AppException.class)
    public CancelOrderResponse cancelOrder(Long orderId) {
        Order order = entityValidatorUtil.requireOrder(orderId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (!CheckRole.isAdmin() && !order.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return CancelOrderResponse.builder()
                    .orderId(orderId)
                    .message("Order already cancelled")
                    .refundSuccess(false)
                    .build();
        }
        // Lấy payment mới nhất
        Payment lastPayment = order.getPayments().stream()
                .max(Comparator.comparing(Payment::getCreatedAt))
                .orElse(null);
        PaymentMethod paymentMethod = lastPayment != null
                ? lastPayment.getPaymentMethod()
                : PaymentMethod.COD;

        // Khởi tạo các trường refund
        boolean refundSuccess = false;
        String refundCode = null;
        String refundMsg = null;
        String refundTxnNo = null;
        BigDecimal refundAmount = null;
        String refundTime = null;

        // Hủy đơn COD
        if (paymentMethod == PaymentMethod.COD) {
            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PROCESSING) {
                throw new AppException(ErrorCode.ORDER_BY_COD_CANNOT_BE_CANCELLED);
            }
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            return CancelOrderResponse.builder()
                    .orderId(orderId)
                    .message("Cancel order successfully")
                    .refundSuccess(true)
                    .build();
        }

        // Hủy đơn VNPAY
        if (paymentMethod == PaymentMethod.VNPAY) {
            // Kiểm tra trạng thái trước khi refund
            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PROCESSING) {
                throw new AppException(ErrorCode.ORDER_BY_VNPAY_CANNOT_BE_CANCELLED);
            }

            // Nếu thanh toán thành công, gọi refund
            if (lastPayment != null && lastPayment.getStatus() == PaymentStatus.SUCCESS) {
                RefundResult refundResponse = paymentService.refundPayment(lastPayment.getId());
                if (refundResponse.getRefundStatus() != PaymentStatus.REFUNDED) {
                    throw new AppException(ErrorCode.REFUND_FAILED);
                }

                refundSuccess = true;
                refundCode = refundResponse.getVnpResponseCode();
                refundMsg = "Refund successful";
                refundTxnNo = refundResponse.getTransactionId();
                refundAmount = refundResponse.getRefundAmount();
                refundTime = refundResponse.getRefundDate() != null
                        ? refundResponse.getRefundDate().toString()
                        : null;
            }
            // Sau khi refund, đổi trạng thái
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            return CancelOrderResponse.builder()
                    .orderId(order.getId())
                    .message("Cancel order successfully")
                    .refundSuccess(refundSuccess)
                    .refundCode(refundCode)
                    .refundMessage(refundMsg)
                    .refundTransactionNo(refundTxnNo)
                    .originalTxnRef(lastPayment != null ? lastPayment.getTxnRef() : null)
                    .amount(refundAmount)
                    .refundTime(refundTime)
                    .build();
        }
        throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING -> next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
            case PROCESSING -> next == OrderStatus.ON_DELIVERY || next == OrderStatus.CANCELLED;
            case ON_DELIVERY -> next == OrderStatus.DELIVERED;
            default -> false;
        };
    }

}
