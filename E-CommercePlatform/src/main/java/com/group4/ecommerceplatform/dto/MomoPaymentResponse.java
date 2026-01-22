package com.group4.ecommerceplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO để nhận response từ MoMo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomoPaymentResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private Long amount;
    private Long responseTime;
    private String message;
    private Integer resultCode;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
    private String signature;
}
