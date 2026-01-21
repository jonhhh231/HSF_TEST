package com.group4.ecommerceplatform.controllers.client;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.services.client.ProductService;
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

@Controller
public class ClientProductController {

    @Autowired
    @Qualifier("clientProductService")
    private ProductService productService;

    @GetMapping("/")
    public String homePage() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String showProducts(Model model,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "12") int size,
                              @RequestParam(value = "category", required = false) Integer categoryId,
                              @RequestParam(value = "search", required = false) String search) {

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

        return "client/products";
    }

    @GetMapping("/products/{id}")
    public String showProductDetail(Model model, @PathVariable("id") Integer id) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "client/product-detail";
    }
}
