package com.group4.ecommerceplatform.services.payment;

import com.group4.ecommerceplatform.dto.MomoIPNRequest;
import com.group4.ecommerceplatform.dto.MomoPaymentResponse;
import com.group4.ecommerceplatform.entities.Order;

/**
 * Service xử lý thanh toán MoMo
 */
public interface MomoPaymentService {

    /**
     * Tạo URL thanh toán MoMo cho đơn hàng
     *
     * @param order Đơn hàng cần thanh toán
     * @return URL để redirect người dùng đến trang thanh toán MoMo
     * @throws Exception nếu có lỗi khi tạo thanh toán
     */
    String createPaymentUrl(Order order) throws Exception;

    /**
     * Xử lý IPN callback từ MoMo (server-to-server)
     *
     * @param ipnRequest Dữ liệu IPN từ MoMo
     * @return true nếu xử lý thành công
     */
    boolean handleIPNCallback(MomoIPNRequest ipnRequest);

    /**
     * Xử lý return URL sau khi người dùng thanh toán xong
     *
     * @param orderId Mã đơn hàng
     * @param resultCode Mã kết quả từ MoMo
     * @return Order đã được cập nhật
     */
    Order handleReturnUrl(String orderId, Integer resultCode);

    /**
     * Kiểm tra signature từ MoMo
     *
     * @param rawSignature Chuỗi dữ liệu để tạo signature
     * @param signature Signature cần kiểm tra
     * @return true nếu signature hợp lệ
     */
    boolean verifySignature(String rawSignature, String signature);
}
