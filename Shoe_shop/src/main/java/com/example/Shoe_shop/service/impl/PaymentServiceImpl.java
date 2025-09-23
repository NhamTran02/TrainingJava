package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.client.VnpayClient;
import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.RefundResult;
import com.example.Shoe_shop.dto.response.VnpayCallbackResponse;
import com.example.Shoe_shop.dto.response.VnpayQueryResponse;
import com.example.Shoe_shop.entity.Order;
import com.example.Shoe_shop.entity.Payment;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.OrderRepository;
import com.example.Shoe_shop.repository.PaymentRepository;
import com.example.Shoe_shop.service.PaymentService;
import com.example.Shoe_shop.utils.TrackingNumberUtil;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import com.example.Shoe_shop.utils.enums.PaymentMethod;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    final PaymentRepository paymentRepository;
    final OrderRepository orderRepository;
    final TrackingNumberUtil trackingNumberUtil;
    final VnpayClient vnpayClient;
    // Bộ nhớ tạm đếm số lần query
    private final Map<String, Integer> queryAttemptMap = new ConcurrentHashMap<>();

    @Value("${vnpay.tmnCode}")
    String tmnCode;
    @Value("${vnpay.secretKey}")
    String vnpay_secretKey;

    @Override
    @Transactional
    public Payment createPayment(Order order, OrderRequest orderRequest) {
        List<Payment> payments = paymentRepository.findByOrOrder_Id(order.getId());
        boolean hasSuccess = payments.stream()
                .anyMatch(p -> p.getStatus() == PaymentStatus.SUCCESS);
        if (hasSuccess) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_SUCCESS);
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_NOT_PAYABLE);
        }
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(orderRequest.getPaymentMethod())
                .amount(order.getTotalAmount())
                .txnRef(order.getTrackingNumber())
                .note(orderRequest.getNote())
                .status(PaymentStatus.PENDING)
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    public String retryVnpayPayment(Long orderId, HttpServletRequest request) {
        Order order=orderRepository.findById(orderId)
                .orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        if(order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_NOT_PAYABLE);
        }
        Payment payment=paymentRepository.findTopByOrder_IdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        // Cho phép thanh toán lại nếu payment đã FAILED hoặc vẫn PENDING
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_SUCCESS);
        }
        Payment newPayment = Payment.builder()
                .order(order)
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .txnRef(trackingNumberUtil.generateUnique())
                .status(PaymentStatus.PENDING)
                .note("Retry payment for previous txnRef: " + payment.getTxnRef())
                .build();

        paymentRepository.save(newPayment);
        return createVnpayPaymentUrl(newPayment, request);
    }

    @Override
    public String createVnpayPaymentUrl(Payment payment, HttpServletRequest request) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TmnCode = tmnCode;
            long amount = payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
            String vnp_TxnRef = payment.getTxnRef();
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
            vnp_Params.put("vnp_ReturnUrl", "http://localhost:8080/api/vnpay/vnpay-callback");
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

            cld.add(Calendar.MINUTE, 15);
            vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = hmacSHA512(vnpay_secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            return "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?" + queryUrl;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL VNPAY", e);
        }
    }


    @Override
    @Transactional
    public VnpayCallbackResponse handleVnpayCallback(Map<String, String> params) {
        // Xác thực chữ ký
        String secureHash = params.remove("vnp_SecureHash");
        String hashData = buildHashDataString(params);
        String checkHash = hmacSHA512(vnpay_secretKey, hashData);
        if (!checkHash.equalsIgnoreCase(secureHash)) {
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo=params.get("vnp_TransactionNo");

        Payment payment = paymentRepository.findByTxnRefForUpdate(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        Order order = payment.getOrder();

        // Nếu payment đã xử lý, trả thông tin hiện tại
        if (payment.getStatus() != PaymentStatus.PENDING) {
            if (payment.getTransactionNo() == null && transactionNo != null) {
                payment.setTransactionNo(transactionNo);
                paymentRepository.save(payment);
            }
            return VnpayCallbackResponse.builder()
                    .orderId(order.getId())
                    .trackingNumber(order.getTrackingNumber())
                    .orderStatus(order.getStatus())
                    .paymentStatus(payment.getStatus())
                    .amount(payment.getAmount())
                    .message("Payment already processed")
                    .build();
        }

        // Cập nhật trạng thái theo mã phản hồi
        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setResponseCode(responseCode);
            payment.setTransactionNo(transactionNo);
            payment.setPayDate(java.time.LocalDateTime.now());
            order.setStatus(OrderStatus.PROCESSING);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setResponseCode(responseCode);
        }

        orderRepository.save(order);
        paymentRepository.save(payment);

        // Trả về thông tin callback
        return VnpayCallbackResponse.builder()
                .orderId(order.getId())
                .trackingNumber(order.getTrackingNumber())
                .orderStatus(order.getStatus())
                .paymentStatus(payment.getStatus())
                .amount(payment.getAmount())
                .message(payment.getStatus() == PaymentStatus.SUCCESS
                        ? "Thanh toán thành công"
                        : "Thanh toán thất bại")
                .build();
    }

    @Override
    @Transactional
    public void cancelExpiredVnpayPayments() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        List<Payment> expired = paymentRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, deadline);

        for (Payment payment : expired) {
            Order order = payment.getOrder();
            if (payment.getPaymentMethod().name().equals("VNPAY")
                    && order.getStatus() == OrderStatus.PENDING) {
                payment.setStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                paymentRepository.save(payment);
            }
        }
    }

    @Override
    @Transactional
    public void reconcilePendingVnpayPayments() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Payment> pendings = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, threshold);

        for (Payment p : pendings) {
            try {
                // Đếm lần query
                int count = queryAttemptMap.getOrDefault(p.getTxnRef(), 0);
                if (count > 5) { // đã thử quá 5 lần → bỏ qua lần này
                    continue;
                }

                Optional<Payment> lockedOpt = paymentRepository.findByTxnRefForUpdate(p.getTxnRef());
                if (lockedOpt.isEmpty()) continue;
                Payment payment = lockedOpt.get();

                if (payment.getStatus() != PaymentStatus.PENDING) continue;

                VnpayQueryResponse resp = vnpayClient.queryTransaction(payment.getTxnRef(), payment.getCreatedAt());
                queryAttemptMap.put(p.getTxnRef(), count + 1);

                if (resp == null) continue;

                Order order = payment.getOrder();
                if (resp.isSuccess()) {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    order.setStatus(OrderStatus.PROCESSING);
                    orderRepository.save(order);
                    paymentRepository.save(payment);
                } else if (resp.isFailed()) {
                    payment.setStatus(PaymentStatus.FAILED);
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    paymentRepository.save(payment);
                }
            } catch (Exception ex) {
                log.error("Reconcile failed for txnRef={}", p.getTxnRef(), ex);
            }
        }
    }

    @Override
    @Transactional
    public VnpayQueryResponse manualCheckVnpayPayment(Long orderId) {
        // Lấy payment gần nhất của order
        Payment payment = paymentRepository.findTopByOrder_IdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        // Gửi request truy vấn VNPAY
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "querydr");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_TxnRef", payment.getTxnRef());
        params.put("vnp_OrderInfo", "Truy vấn trạng thái thanh toán");
        params.put("vnp_TransDate",
                payment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        // sắp xếp, ký hash giống create URL
        String hashData = buildHashDataString(params);
        String secureHash = hmacSHA512(vnpay_secretKey, hashData);
        params.put("vnp_SecureHash", secureHash);

        // Gửi HTTP GET đến VNPAY query API
        String queryUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction?"
                + params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(queryUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                String body = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                        .lines().collect(Collectors.joining());
                // Parse JSON trả về từ VNPAY
                VnpayQueryResponse resp = parseVnpayQueryResponse(body);

                // Nếu VNPAY trả về thành công, cập nhật DB
                if ("00".equals(resp.getVnpResponseCode())) {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.getOrder().setStatus(OrderStatus.PROCESSING);
                } else if (!PaymentStatus.SUCCESS.equals(payment.getStatus())) {
                    payment.setStatus(PaymentStatus.FAILED);
                    payment.getOrder().setStatus(OrderStatus.CANCELLED);
                }
                orderRepository.save(payment.getOrder());
                paymentRepository.save(payment);
                return resp;
            } else {
                return null; // Không nhận được phản hồi
            }
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi truy vấn VNPAY", ex);
        }
    }

    @Override
    @Transactional
    public RefundResult refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new AppException(ErrorCode.PAYMENT_CANNOT_REFUND);
        }
        if (payment.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_NOT_SUPPORT_REFUND);
        }

        try {
            String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            String createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String transactionDate = payment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String ipAddr = InetAddress.getLocalHost().getHostAddress();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "UNKNOWN";

            // Lấy transactionNo từ payment, có thể null
            String transactionNo = payment.getTransactionNo() != null ? payment.getTransactionNo() : "";

            Map<String, String> params = new LinkedHashMap<>();
            params.put("vnp_RequestId", requestId);
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "refund");
            params.put("vnp_TmnCode", tmnCode);
            params.put("vnp_TransactionType", "02");
            params.put("vnp_TxnRef", payment.getTxnRef());
            params.put("vnp_Amount", payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue() + "");
            params.put("vnp_TransactionNo", transactionNo);
            params.put("vnp_TransactionDate", transactionDate);
            params.put("vnp_CreateBy", username);
            params.put("vnp_CreateDate", createDate);
            params.put("vnp_IpAddr", ipAddr);
            params.put("vnp_OrderInfo", "Refund for order: " + payment.getOrder().getTrackingNumber());

            // Tạo secure hash
            String data = String.join("|",
                    requestId,
                    "2.1.0",
                    "refund",
                    tmnCode,
                    "02",
                    payment.getTxnRef(),
                    params.get("vnp_Amount"),
                    transactionNo,
                    transactionDate,
                    username,
                    createDate,
                    ipAddr,
                    params.get("vnp_OrderInfo")
            );
            String secureHash = hmacSHA512(vnpay_secretKey, data);
            params.put("vnp_SecureHash", secureHash);

            // Gửi request POST JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(params);

            URL url = new URL("https://sandbox.vnpayment.vn/merchant_webapi/api/transaction");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                String body;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    body = br.lines().collect(Collectors.joining());
                }

                Map<String, String> resp = mapper.readValue(body, Map.class);
                String responseCode = resp.get("vnp_ResponseCode");
                String respTransactionNo = resp.get("vnp_TransactionNo"); // mã GD thực tế từ VNPAY

                if ("00".equals(responseCode)) {
                    payment.setStatus(PaymentStatus.REFUNDED);
                    payment.setResponseCode(responseCode);
                    payment.setTransactionNo(respTransactionNo); // cập nhật transactionNo
                    paymentRepository.save(payment);
                } else {
                    throw new RuntimeException("Refund failed. Code: " + responseCode +
                            ", Message: " + resp.get("vnp_Message"));
                }

                return RefundResult.builder()
                        .transactionId(respTransactionNo) // dùng transactionNo thực tế
                        .paymentId(paymentId)
                        .refundStatus(payment.getStatus())
                        .refundAmount(payment.getAmount())
                        .refundDate(LocalDateTime.now())
                        .vnpResponseCode(payment.getResponseCode())
                        .build();
            } else {
                throw new RuntimeException("Cannot connect to VNPAY refund API: HTTP " + conn.getResponseCode());
            }

        } catch (Exception ex) {
            throw new RuntimeException("Refund payment failed: " + ex.getMessage(), ex);
        }
    }

    private VnpayQueryResponse parseVnpayQueryResponse(String body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Jackson sẽ tự map các field nhờ @JsonProperty trong VnpayQueryResponse
            return mapper.readValue(body, VnpayQueryResponse.class);
        } catch (Exception e) {
            // Nên log hoặc wrap exception để dễ debug
            throw new RuntimeException("Lỗi parse phản hồi VNPAY: " + e.getMessage(), e);
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

    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            throw new RuntimeException("hmac error", ex);
        }
    }

    private String buildHashDataString(Map<String, String> params){
        // sắp xếp key
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) continue;
            // Dùng UTF-8 để encode giá trị (và giữ '+' do URLEncoder)
            String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
            if (!first) {
                hashData.append('&');
            }
            hashData.append(fieldName).append('=').append(encodedValue);
            first = false;
        }
        return hashData.toString();
    }

}
