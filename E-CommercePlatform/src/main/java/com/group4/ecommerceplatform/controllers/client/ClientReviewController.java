package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.Review;
import com.group4.ecommerceplatform.services.client.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reviews")
public class ClientReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * API endpoint để lấy reviews của sản phẩm (dùng cho AJAX)
     */
    @GetMapping("/product/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductReviews(@PathVariable Integer productId) {
        try {
            List<Review> reviews = reviewService.getProductReviews(productId);
            Double avgRating = reviewService.getAverageRating(productId);
            Long reviewCount = reviewService.getReviewCount(productId);

            // Convert reviews to map for JSON response
            List<Map<String, Object>> reviewMaps = reviews.stream()
                    .map(review -> {
                        Map<String, Object> reviewMap = new HashMap<>();
                        reviewMap.put("id", review.getId());
                        reviewMap.put("rating", review.getRating());
                        reviewMap.put("comment", review.getComment());
                        reviewMap.put("userName", review.getUser().getFullName());
                        reviewMap.put("userId", review.getUser().getId());
                        return reviewMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviewMaps);
            response.put("averageRating", avgRating);
            response.put("reviewCount", reviewCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Kiểm tra xem user có thể review sản phẩm không
     */
    @GetMapping("/can-review/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> canReviewProduct(
            @PathVariable Integer productId,
            HttpSession session) {
        Integer userId = getCurrentUserId(session);

        Map<String, Boolean> response = new HashMap<>();
        if (userId == null) {
            response.put("canReview", false);
            return ResponseEntity.ok(response);
        }

        boolean canReview = reviewService.canUserReviewProduct(userId, productId);
        response.put("canReview", canReview);
        return ResponseEntity.ok(response);
    }

    /**
     * Submit review mới
     */
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitReview(
            @RequestBody Map<String, Object> requestBody,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = getCurrentUserId(session);
        if (userId == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để review");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Integer productId = (Integer) requestBody.get("productId");
            Integer rating = (Integer) requestBody.get("rating");
            String comment = (String) requestBody.get("comment");

            // Validation
            if (productId == null || rating == null || comment == null || comment.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng điền đầy đủ thông tin");
                return ResponseEntity.badRequest().body(response);
            }

            if (rating < 1 || rating > 5) {
                response.put("success", false);
                response.put("message", "Rating phải từ 1 đến 5");
                return ResponseEntity.badRequest().body(response);
            }

            if (comment.length() < 10 || comment.length() > 1000) {
                response.put("success", false);
                response.put("message", "Comment phải có từ 10 đến 1000 ký tự");
                return ResponseEntity.badRequest().body(response);
            }

            Review review = reviewService.submitReview(userId, productId, rating, comment);
            response.put("success", true);
            response.put("message", "Đánh giá của bạn đã được gửi thành công!");
            response.put("reviewId", review.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private Integer getCurrentUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
}
