package com.group4.ecommerceplatform.apis.admin;

import com.group4.ecommerceplatform.dto.category.CategoryDTO;
import com.group4.ecommerceplatform.responses.SuccessResponse;
import com.group4.ecommerceplatform.services.admin.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
public class CategoryAPI {


    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<SuccessResponse> createCategory(@RequestBody @Valid CategoryDTO dto) {
        categoryService.createCategory(dto);
        SuccessResponse response = new SuccessResponse();
        response.setMessage("Create category success");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateCategory(@RequestBody @Valid CategoryDTO dto, @PathVariable Long id) {
        dto.setId(id);
        categoryService.updateCategory(dto);
        SuccessResponse response = new SuccessResponse();
        response.setMessage("Update category success");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);

        SuccessResponse response = new SuccessResponse();
        response.setMessage("Xóa danh mục thành công");
        response.setData(null);

        return ResponseEntity.ok(response);
    }
}