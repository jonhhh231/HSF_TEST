package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.services.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

//    @GetMapping("")
//    public String listCategories(Model model) {
//        model.addAttribute("categories", categoryService.getAllCategories());
//        // LƯU Ý: Đường dẫn này phải khớp với vị trí file HTML thật của bạn
//        return "admin/pages/category-list";
//    }

    @GetMapping("/create")
    public String showNewForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Thêm mới danh mục");
        return "admin/pages/category-form"; // Bạn cần tạo thêm file này để nhập liệu
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            ra.addFlashAttribute("message", "Danh mục không tồn tại!");
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Sửa danh mục (ID: " + id + ")");
        return "admin/pages/category-form";
    }

    @PostMapping("/save")
    public String saveCategory(Category category, RedirectAttributes ra) {
        categoryService.saveCategory(category);
        ra.addFlashAttribute("message", "Đã lưu thành công!");
        return "redirect:/admin/categories";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteCategoryAjax(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa: " + e.getMessage());
        }
    }

    @GetMapping("")
    public String listCategories(Model model) {
        List<Category> list = categoryService.getAllCategories();
        System.out.println("fing" + list.size());
        if(!list.isEmpty()) {
            System.out.println("first" + list.get(0).getName());
        }
        model.addAttribute("categories", list);
        return "admin/pages/category-list";
    }
}