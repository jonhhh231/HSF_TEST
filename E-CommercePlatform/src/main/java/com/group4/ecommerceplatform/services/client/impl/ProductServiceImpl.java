package com.group4.ecommerceplatform.services.client.impl;

import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.repositories.ProductRepository;
import com.group4.ecommerceplatform.services.client.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("clientProductService")
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable);
    }

    @Override
    public Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts(pageable);
        }
        // Validate keyword length
        if (keyword.trim().length() < 2) {
            throw new IllegalArgumentException("Từ khóa tìm kiếm phải có ít nhất 2 ký tự");
        }
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword.trim(), pageable);
    }

    @Override
    public Product getProductById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        // Validate product is active
        if (!product.getIsActive()) {
            throw new NotFoundException("Sản phẩm này hiện không còn bán");
        }

        return product;
    }
}
