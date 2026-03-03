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

        // Kiểm tra xem user có đơn hàng nào đã giao (DELIVERED) chứa sản phẩm này
        // và chưa review sản phẩm đó trong đơn đó chưa
        List<Order> deliveredOrders = orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .filter(order -> "DELIVERED".equals(order.getShippingStatus()))
                .collect(Collectors.toList());

        for (Order order : deliveredOrders) {
            if (order.getOrderDetails() != null) {
                boolean hasProduct = order.getOrderDetails().stream()
                        .anyMatch(detail -> detail.getProductId().equals(productId));
                if (hasProduct) {
                    // Allow review if this specific (user, product, order) hasn't been reviewed yet
                    if (!reviewRepository.existsByUserAndProductAndOrder(user, product, order)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    @Transactional
    public Review submitReview(Integer userId, Integer orderId, Integer productId, Integer rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // 1. Load order and verify ownership
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("Đơn hàng này không thuộc về bạn");
        }

        // 2. Gate: only allow review when order is DELIVERED
        if (!"DELIVERED".equals(order.getShippingStatus())) {
            throw new IllegalStateException("Chỉ có thể đánh giá sau khi đơn hàng đã được giao");
        }

        // 3. Verify product is actually in this order
        if (order.getOrderDetails() == null || order.getOrderDetails().stream()
                .noneMatch(detail -> detail.getProductId().equals(productId))) {
            throw new IllegalArgumentException("Sản phẩm này không có trong đơn hàng");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // 4. Prevent duplicate review for this specific (user, order, product)
        if (reviewRepository.existsByUserAndProductAndOrder(user, product, order)) {
            throw new IllegalStateException("Bạn đã đánh giá sản phẩm này trong đơn hàng này rồi");
        }

        // 5. Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating phải từ 1 đến 5");
        }

        // 6. Validate comment
        if (comment == null || comment.trim().length() < 10) {
            throw new IllegalArgumentException("Nhận xét phải có ít nhất 10 ký tự");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setOrder(order);
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

    @Override
    public boolean hasUserReviewedProductForOrder(Integer userId, Integer orderId, Integer productId) {
        if (userId == null || orderId == null || productId == null) {
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        Order order = orderRepository.findById(orderId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || order == null || product == null) {
            return false;
        }

        return reviewRepository.existsByUserAndProductAndOrder(user, product, order);
    }
}
