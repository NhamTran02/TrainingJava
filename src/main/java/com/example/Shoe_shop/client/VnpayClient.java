package com.example.Shoe_shop.client;

import com.example.Shoe_shop.dto.response.VnpayQueryResponse;
import com.example.Shoe_shop.service.impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class VnpayClient {
    private final RestTemplate rest = new RestTemplate();

    @Value("${vnpay.query-url}")
    private String queryUrl;

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.secretKey}")
    private String secretKey;

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public VnpayQueryResponse queryTransaction(String txnRef, LocalDateTime transactionDate) {
        try {
            String vnp_RequestId = String.valueOf(System.currentTimeMillis()); // unique per day required
            String vnp_Version = "2.1.0";
            String vnp_Command = "querydr";
            String vnp_TransactionDate = transactionDate.format(TF);
            String vnp_CreateDate = LocalDateTime.now().format(TF);
            String ipAddr = InetAddress.getLocalHost().getHostAddress();
            String vnp_OrderInfo = "Query transaction " + txnRef;

            // theo doc: data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" + vnp_TxnRef + "|" + vnp_TransactionDate + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;
            String data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, tmnCode, txnRef, vnp_TransactionDate, vnp_CreateDate, ipAddr, vnp_OrderInfo);
            String secureHash = PaymentServiceImpl.hmacSHA512(secretKey, data);

            Map<String, Object> body = new HashMap<>();
            body.put("vnp_RequestId", vnp_RequestId);
            body.put("vnp_Version", vnp_Version);
            body.put("vnp_Command", vnp_Command);
            body.put("vnp_TmnCode", tmnCode);
            body.put("vnp_TxnRef", txnRef);
            body.put("vnp_TransactionDate", vnp_TransactionDate);
            body.put("vnp_CreateDate", vnp_CreateDate);
            body.put("vnp_IpAddr", ipAddr);
            body.put("vnp_OrderInfo", vnp_OrderInfo);
            body.put("vnp_SecureHash", secureHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

            ResponseEntity<VnpayQueryResponse> resp = rest.exchange(queryUrl, HttpMethod.POST, req, VnpayQueryResponse.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                return resp.getBody();
            } else {
                log.warn("VNPAY query returned non-200: {}", resp.getStatusCodeValue());
                return null;
            }
        } catch (Exception ex) {
            log.error("VNPAY queryTransaction error for txnRef=" + txnRef, ex);
            return null;
        }
    }
}
