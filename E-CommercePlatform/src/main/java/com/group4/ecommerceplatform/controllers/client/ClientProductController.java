package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.services.client.CartService;
import com.group4.ecommerceplatform.services.client.ProductService;
import com.group4.ecommerceplatform.services.client.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ClientProductController {

    @Autowired
    @Qualifier("clientProductService")
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String homePage() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String showProducts(Model model,
                              HttpSession session,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "12") int size,
                              @RequestParam(value = "category", required = false) Integer categoryId,
                              @RequestParam(value = "search", required = false) String search) {

        // Add user to model for template access
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("currentUser", user);
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage;

        if (categoryId != null) {
            productPage = productService.getProductsByCategory(categoryId, pageable);
        } else if (search != null && !search.isEmpty()) {
            productPage = productService.searchProducts(search, pageable);
        } else {
            productPage = productService.getAllProducts(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("search", search);

        // Lấy ratings cho tất cả sản phẩm trong trang (1 query duy nhất)
        List<Integer> productIds = productPage.getContent().stream()
            .map(Product::getId)
            .collect(Collectors.toList());

        Map<Integer, Double> ratings = reviewService.getAverageRatingsForProducts(productIds);
        Map<Integer, Long> reviewCounts = reviewService.getReviewCountsForProducts(productIds);

        model.addAttribute("productRatings", ratings);
        model.addAttribute("productReviewCounts", reviewCounts);

        return "client/products";
    }

    @GetMapping("/products/{id}")
    public String showProductDetail(Model model,
                                   HttpSession session,
                                   @PathVariable("id") Integer id) {

        // Add user to model for template access
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("currentUser", user);
            // Initialize cart count
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                int cartCount = cartService.getCartItemCount(userId);
                session.setAttribute("cartItemCount", cartCount);
            }
        } else {
            session.setAttribute("cartItemCount", 0);
        }

        Product product = productService.getProductById(id);
        model.addAttribute("product", product);

        // Get related products from same category
        if (product.getCategory() != null) {
            Pageable pageable = PageRequest.of(0, 4); // Get 4 related products
            Page<Product> relatedPage = productService.getProductsByCategory(
                product.getCategory().getId(),
                pageable
            );
            // Filter out current product from related products
            model.addAttribute("relatedProducts",
                relatedPage.getContent().stream()
                    .filter(p -> !p.getId().equals(id))
                    .limit(4)
                    .toList()
            );
        }

        return "client/product-detail";
    }
}
