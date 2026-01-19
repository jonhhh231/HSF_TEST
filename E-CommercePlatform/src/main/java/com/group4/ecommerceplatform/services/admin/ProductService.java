package com.group4.ecommerceplatform.services.admin;

import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.responses.admin.PageDataResponse;
import com.group4.ecommerceplatform.responses.admin.ProductSearchResponse;
import org.springframework.data.domain.Pageable;


public interface ProductService {
    public void createProduct(ProductDTO productDTO);
    public ProductDTO getProductById(Long id);
    public PageDataResponse<ProductSearchResponse> getProductList(Pageable pageable);
}
