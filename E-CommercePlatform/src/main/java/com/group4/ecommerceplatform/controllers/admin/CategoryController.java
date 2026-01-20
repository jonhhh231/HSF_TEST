package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.dto.category.CategoryDTO;
import com.group4.ecommerceplatform.services.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/create")
    public String getCategoryCreatePage(Model model) {
        return "admin/pages/category-create";
    }

    @GetMapping("/detail/{id}")
    public String getCategoryUpdatePage(Model model, @PathVariable("id") Long id) {
        CategoryDTO dto = categoryService.getCategoryById(id);
        model.addAttribute("category", dto);
        return "admin/pages/category-detail";
    }

    @GetMapping
    public String getCategoryListPage(Model model) {
        List<CategoryDTO> list = categoryService.getAllCategories();

        model.addAttribute("categoryList", list);

        return "admin/pages/category-list";
    }
}