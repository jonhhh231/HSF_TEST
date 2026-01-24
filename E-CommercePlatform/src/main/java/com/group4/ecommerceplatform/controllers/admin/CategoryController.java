package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.services.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/create")
    public String getCreateCategoryPage() {
        return "admin/pages/category-create";
    }
    @PostMapping
    public String createCategory(@ModelAttribute  Category category) {
        categoryService.createCategory(category);
        return "redirect:/admin/categories/create";
    }
}
