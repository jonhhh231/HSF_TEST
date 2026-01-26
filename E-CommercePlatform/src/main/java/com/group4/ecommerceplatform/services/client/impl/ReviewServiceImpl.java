package com.group4.ecommerceplatform.services.client.impl;

import com.group4.ecommerceplatform.entities.*;
import com.group4.ecommerceplatform.repositories.*;
import com.group4.ecommerceplatform.services.client.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    @Override
    public List<Review> getProductReviews(Integer productId) {
        if (productId == null) {
            return List.of();
        }
        return reviewRepository.findByProductIdOrderByIdDesc(productId);
    }

    @Override
    public boolean canUserReviewProduct(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || product == null) {
            return false;
        }

        // Kiểm tra xem user đã review sản phẩm này chưa
        if (reviewRepository.existsByUserAndProduct(user, product)) {
            return false;
        }

        // Kiểm tra xem user có đơn hàng nào đã thanh toán chứa sản phẩm này không
        List<Order> paidOrders = orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .filter(order -> "PAID".equals(order.getPaymentStatus()))
                .collect(Collectors.toList());

        for (Order order : paidOrders) {
            if (order.getOrderDetails() != null) {
                boolean hasProduct = order.getOrderDetails().stream()
                        .anyMatch(detail -> detail.getProductId().equals(productId));
                if (hasProduct) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Transactional
    public Review submitReview(Integer userId, Integer productId, Integer rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Kiểm tra quyền review
        if (!canUserReviewProduct(userId, productId)) {
            throw new RuntimeException("Bạn không có quyền review sản phẩm này hoặc đã review rồi");
        }

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating phải từ 1 đến 5");
        }

        // Validate comment
        if (comment == null || comment.trim().length() < 10) {
            throw new IllegalArgumentException("Nhận xét phải có ít nhất 10 ký tự");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment.trim());

        return reviewRepository.save(review);
    }

    @Override
    public boolean hasUserReviewedProduct(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || product == null) {
            return false;
        }

        return reviewRepository.existsByUserAndProduct(user, product);
    }
}
