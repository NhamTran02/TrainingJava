package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.controller.VnpayController;
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
import com.example.Shoe_shop.utils.enums.PaymentMethod;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {
    final UserRepository userRepository;
    final OrderRepository orderRepository;
    final CartJdbcRepository cartJdbcRepository;
    final PurchaseOrderItemRepository purchaseOrderItemRepository;
    final EntityValidatorUtil entityValidatorUtil;
    final TrackingNumberUtil  trackingNumberUtil;
    final OrderMapper orderMapper;
    final VnpayController vnpayController;
    final PaymentRepository paymentRepository;

    @Value("${vnpay.tmnCode}")
    String tmnCode;
    @Value("${vnpay.secretKey}")
    String vnpay_secretKey;


    @Override
    @Transactional
    public OrderResponse createOrderFromCart(Long userId, OrderRequest orderRequest,HttpServletRequest request) {
        User user=entityValidatorUtil.requireUser(userId);
        Long cartId= cartJdbcRepository.getCartIdByUserId(user.getId());

        List<CartItemResponse> cartItems = cartJdbcRepository.findCartItems(cartId)
                .stream()
                .filter(CartItemResponse::getSelected)
                .toList();
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

        order.setTotalAmount(totalAmount.add(order.getShippingFee()));
        order.setTotalCost(totalCost);

        Order savedOrder = orderRepository.save(order);
        Payment payment=Payment.builder()
                .order(savedOrder)
                .paymentMethod(orderRequest.getPaymentMethod())
                .amount(savedOrder.getTotalAmount())
                .txnRef(savedOrder.getTrackingNumber())
                .note(savedOrder.getNote())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        OrderResponse response=orderMapper.toResponse(savedOrder);
        if (orderRequest.getPaymentMethod()==PaymentMethod.VNPAY){
            String payUrl=createVnpayPaymentUrl(savedOrder,request);
            response.setPaymentUrl(payUrl);
        }
        response.setTrackingNumber(trackingNumberUtil.generateUnique());
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
        return switch (current) {
            case PENDING -> next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
            case PROCESSING -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            default -> false;
        };
    }

    private String createVnpayPaymentUrl(Order order,HttpServletRequest request) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TmnCode = tmnCode;
            long amount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();
            String vnp_TxnRef = order.getTrackingNumber();
            String vnp_IpAddr = getIpAddress(request);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_Locale","vn");
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_ReturnUrl", "http://localhost:8080/api/vnpay/vnpay-callback"); // callback
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

            cld.add(Calendar.SECOND, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Sắp xếp key, tạo hashData
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = vnpayController.hmacSHA512(vnpay_secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            System.out.println( "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?" + queryUrl );
            return "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?" + queryUrl;


        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL VNPAY", e);
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
                ipAddress = "127.0.0.1";
            }
        } catch (Exception e) {
            ipAddress = "Invalid IP:" + e.getMessage();
        }
        return ipAddress;
    }


}
