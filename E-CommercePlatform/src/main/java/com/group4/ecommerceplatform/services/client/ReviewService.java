package com.group4.ecommerceplatform.services.client;

import com.group4.ecommerceplatform.entities.Review;

import java.util.List;
import java.util.Map;

/**
 * Service để xử lý Review/Rating
 */
public interface ReviewService {

    /**
     * Lấy rating trung bình của một sản phẩm
     */
    Double getAverageRating(Integer productId);

    /**
     * Lấy số lượng reviews của một sản phẩm
     */
    Long getReviewCount(Integer productId);

    /**
     * Lấy rating cho nhiều sản phẩm cùng lúc (để tránh N+1 query)
     * @return Map với key là productId, value là rating trung bình
     */
    Map<Integer, Double> getAverageRatingsForProducts(Iterable<Integer> productIds);

    /**
     * Lấy số lượng reviews cho nhiều sản phẩm cùng lúc
     * @return Map với key là productId, value là số lượng reviews
     */
    Map<Integer, Long> getReviewCountsForProducts(Iterable<Integer> productIds);

    /**
     * Lấy tất cả reviews của một sản phẩm
     */
    List<Review> getProductReviews(Integer productId);

    /**
     * Kiểm tra xem user có thể review sản phẩm này không
     * (đã mua và đơn hàng đã thanh toán, chưa review trước đó)
     */
    boolean canUserReviewProduct(Integer userId, Integer productId);

    /**
     * Submit review mới
     */
    Review submitReview(Integer userId, Integer productId, Integer rating, String comment);

    /**
     * Kiểm tra xem user đã review sản phẩm này chưa
     */
    boolean hasUserReviewedProduct(Integer userId, Integer productId);
}
