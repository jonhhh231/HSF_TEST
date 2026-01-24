package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.services.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    @Qualifier("adminCategoryService")
    private CategoryService categoryService;
    @GetMapping("/create")
    public String getCreateCategoryPage() {
        return "admin/pages/category-create";
    }
    @GetMapping
    public String getCategoryListPage(Model model) {
        List<Category> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "admin/pages/category-list";
    }
    @GetMapping("/detail/{id}")
    public String getCategoryDetailPage(@PathVariable("id") Integer id, Model model) {
        try {
            Category category = categoryService.getCategoryById(id);
            model.addAttribute("category", category);
            return  "admin/pages/category-detail";
        } catch (RuntimeException e) {
            model.addAttribute("message", "Category Not Found");
            return "admin/pages/errors/404";
        }
    }
    @PostMapping("/update/{id}")
    public String updateCategory(@ModelAttribute Category category,  @PathVariable("id") Integer id) {
        category.setId(id);
        categoryService.updateCategory(category);
        return "redirect:/admin/categories";
    }
    @PostMapping
    public String createCategory(@ModelAttribute  Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/categories/create";
    }

}
