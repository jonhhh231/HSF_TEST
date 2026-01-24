package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.services.admin.CategoryService;
import com.group4.ecommerceplatform.services.admin.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @Autowired
    @Qualifier("adminProductService")
    private ProductService productService;
    @Autowired
    @Qualifier("adminCategoryService")
    private CategoryService categoryService;
    @GetMapping
    public String getProductListPage(Model model,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "page", required = false, defaultValue = "1") String page,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") String size){
        keyword = keyword==null?"":keyword.trim();

        int pageNumber = Integer.parseInt(page);
        int pageSize = Integer.parseInt(size);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Product> productPage = productService.getProductList(keyword, pageable);
        model.addAttribute("productList", productPage.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword",  keyword);
        return "admin/pages/product-list";
    }

    @GetMapping("/create")
    public String getProductCreatePage(Model model){
        List<Category> categoryList = categoryService.getAllCategories();
        model.addAttribute("product", new Product());
        model.addAttribute("categoryList", categoryList);
        return "admin/pages/product-create";
    }

    @PostMapping
    public String createProduct(@ModelAttribute Product product){
        productService.saveProduct(product);
        return "redirect:/admin/products/create";
    }

    @GetMapping("/detail/{id}")
    public String getProductUpdatePage(Model model, @PathVariable("id") Integer id){
        Product product = productService.getProductById(id);
        List<Category> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("product", product);
        return "admin/pages/product-detail";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Integer id, @ModelAttribute Product product){
        product.setId(id);
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }
}
