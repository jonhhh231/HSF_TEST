package com.group4.ecommerceplatform.services.admin.impl;

import com.group4.ecommerceplatform.converters.product.ProductDTOConverter;
import com.group4.ecommerceplatform.converters.product.ProductResponseConverter;
import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.entities.Category;
import com.group4.ecommerceplatform.entities.Product;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.repositories.CategoryRepository;
import com.group4.ecommerceplatform.repositories.ProductRepository;
import com.group4.ecommerceplatform.responses.admin.MetaPagination;
import com.group4.ecommerceplatform.responses.admin.PageDataResponse;
import com.group4.ecommerceplatform.responses.admin.ProductSearchResponse;
import com.group4.ecommerceplatform.services.admin.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductResponseConverter productResponseConverter;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductDTOConverter productDTOConverter;
    @Transactional
    @Override
    public void createProduct(ProductDTO productDTO) {
        // convert to product entity
        Product product = productDTOConverter.toProductEntity(productDTO);
        // check category exist
        if(productDTO.getCategoryId() != null){
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new NotFoundException("Category Not Found"));
            product.setCategory(category);
        }
        // save to db
        productRepository.save(product);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product Not Found"));
        return productDTOConverter.toProductDTO(product);
    }

    @Override
    public PageDataResponse<ProductSearchResponse> getProductList(Pageable pageable) {
        Page<Product> productList = productRepository.findAll(pageable);
        MetaPagination metaPagination = new MetaPagination();
        metaPagination.setPage(pageable.getPageNumber() + 1);
        metaPagination.setPages(productList.getTotalPages());
        metaPagination.setTotal(productList.getTotalElements());
        metaPagination.setLimit(pageable.getPageSize());
        PageDataResponse<ProductSearchResponse> pageDataResponse = new PageDataResponse<>();
        pageDataResponse.setMetaPagination(metaPagination);
        pageDataResponse.setData(productList.get().map(item -> productResponseConverter.toProductSearchResponse(item)).toList());
        return pageDataResponse;
    }
}
