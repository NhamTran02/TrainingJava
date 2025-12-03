package com.example.Shoe_shop.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VnpayQueryResponse {
    @JsonProperty("vnp_ResponseCode")
    private String vnpResponseCode;

    @JsonProperty("vnp_TransactionStatus")
    private String vnpTransactionStatus;

    @JsonProperty("vnp_TransactionNo")
    private String vnpTransactionNo;

    @JsonProperty("vnp_Amount")
    private String vnpAmount;

    @JsonProperty("vnp_PayDate")
    private String vnpPayDate;

    @JsonProperty("vnp_ResponseId")
    private String vnpResponseId;

    @JsonProperty("vnp_SecureHash")
    private String vnpSecureHash;

    // helper
    public boolean isSuccess() {
        return "00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus);
    }

    public boolean isFailed() {
        // Nếu responseCode != 00 và khác mã pending (ví dụ 91), coi là failed
        return vnpResponseCode != null && !"00".equals(vnpResponseCode) && !"91".equals(vnpResponseCode);
    }
}
