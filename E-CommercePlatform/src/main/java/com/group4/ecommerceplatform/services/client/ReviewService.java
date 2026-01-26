package com.group4.ecommerceplatform.services.client;

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
}
