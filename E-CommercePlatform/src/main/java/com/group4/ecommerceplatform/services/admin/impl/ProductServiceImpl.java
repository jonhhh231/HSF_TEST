package com.group4.ecommerceplatform.services.admin.impl;

import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.repositories.CategoryRepository;
import com.group4.ecommerceplatform.repositories.ProductRepository;
import com.group4.ecommerceplatform.services.admin.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("adminProductService")
public class ProductServiceImpl implements ProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Override
    public void saveProduct(Product product) {
        validateProduct(product);
        if(product.getCategory() != null && product.getCategory().getId() != null){
            Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục"));
            product.setCategory(category);
        }
        productRepository.save(product);
    }
    @Override
    public Product getProductById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
    }

    @Override
    public Page<Product> getProductList(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }

    /**
     * Validate product data
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Thông tin sản phẩm không được để trống");
        }

        // Validate name
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (product.getName().trim().length() < 3) {
            throw new IllegalArgumentException("Tên sản phẩm phải có ít nhất 3 ký tự");
        }
        if (product.getName().length() > 255) {
            throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 255 ký tự");
        }

        // Validate description
        if (product.getDescription() != null && product.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Mô tả sản phẩm không được vượt quá 1000 ký tự");
        }

        // Validate price
        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Giá sản phẩm không được để trống");
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0");
        }
        if (product.getPrice().compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("Giá sản phẩm quá lớn");
        }

        // Validate stock quantity
        if (product.getStockQuantity() == null) {
            throw new IllegalArgumentException("Số lượng tồn kho không được để trống");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được nhỏ hơn 0");
        }
        if (product.getStockQuantity() > 999999) {
            throw new IllegalArgumentException("Số lượng tồn kho quá lớn");
        }

        // Validate category
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new IllegalArgumentException("Danh mục sản phẩm không được để trống");
        }

        // Validate URL if provided
        if (product.getUrl() != null && !product.getUrl().trim().isEmpty()) {
            if (product.getUrl().length() > 500) {
                throw new IllegalArgumentException("URL không được vượt quá 500 ký tự");
            }
        }
    }
}
