package com.group4.ecommerceplatform.services.client.impl;

import com.group4.ecommerceplatform.entities.Review;
import com.group4.ecommerceplatform.repositories.ReviewRepository;
import com.group4.ecommerceplatform.services.client.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation của ReviewService
 * Sử dụng query trực tiếp để tránh lazy loading issues
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Double getAverageRating(Integer productId) {
        if (productId == null) {
            return 0.0;
        }

        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);

        // Nếu không có review nào, avgRating sẽ là null
        if (avgRating == null) {
            return 0.0;
        }

        // Làm tròn 1 chữ số thập phân
        return Math.round(avgRating * 10.0) / 10.0;
    }

    @Override
    public Long getReviewCount(Integer productId) {
        if (productId == null) {
            return 0L;
        }

        return reviewRepository.countByProductId(productId);
    }

    @Override
    public Map<Integer, Double> getAverageRatingsForProducts(Iterable<Integer> productIds) {
        Map<Integer, Double> ratingsMap = new HashMap<>();

        if (productIds == null) {
            return ratingsMap;
        }

        // Fetch tất cả reviews trong 1 query duy nhất
        List<Review> reviews = reviewRepository.findByProductIdIn(productIds);

        // Group by product ID và tính average
        Map<Integer, List<Review>> reviewsByProduct = reviews.stream()
            .collect(Collectors.groupingBy(r -> r.getProduct().getId()));

        // Tính rating trung bình cho mỗi sản phẩm
        for (Integer productId : productIds) {
            List<Review> productReviews = reviewsByProduct.get(productId);

            if (productReviews == null || productReviews.isEmpty()) {
                ratingsMap.put(productId, 0.0);
            } else {
                double avg = productReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
                // Làm tròn 1 chữ số thập phân
                ratingsMap.put(productId, Math.round(avg * 10.0) / 10.0);
            }
        }

        return ratingsMap;
    }

    @Override
    public Map<Integer, Long> getReviewCountsForProducts(Iterable<Integer> productIds) {
        Map<Integer, Long> countsMap = new HashMap<>();

        if (productIds == null) {
            return countsMap;
        }

        // Fetch tất cả reviews trong 1 query duy nhất
        List<Review> reviews = reviewRepository.findByProductIdIn(productIds);

        // Group by product ID và đếm
        Map<Integer, Long> countsByProduct = reviews.stream()
            .collect(Collectors.groupingBy(
                r -> r.getProduct().getId(),
                Collectors.counting()
            ));

        // Đảm bảo tất cả productIds đều có trong map (0 nếu không có review)
        for (Integer productId : productIds) {
            countsMap.put(productId, countsByProduct.getOrDefault(productId, 0L));
        }

        return countsMap;
    }
}
