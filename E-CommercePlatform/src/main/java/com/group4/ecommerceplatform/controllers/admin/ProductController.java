package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.services.admin.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public String getProductListPage(Model model,
                                    @RequestParam(value = "page", required = false, defaultValue = "1") String page,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") String size){
        int pageNumber = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Product> productPage = productService.getProductList(pageable);
        model.addAttribute("productList", productPage.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        return "admin/pages/product-list";
    }

    @GetMapping("/create")
    public String getProductCreatePage(Model model){
        model.addAttribute("product", new Product());
        return "admin/pages/product-create";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product){
        productService.createProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/detail/{id}")
    public String getProductUpdatePage(Model model, @PathVariable("id") Integer id){
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/pages/product-detail";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Integer id, @ModelAttribute Product product){
        product.setId(id);
        productService.createProduct(product);
        return "redirect:/admin/products";
    }
}
