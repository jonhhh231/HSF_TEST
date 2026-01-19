package com.group4.ecommerceplatform.controllers.admin;

import com.group4.ecommerceplatform.dto.product.ProductDTO;
import com.group4.ecommerceplatform.responses.admin.PageDataResponse;
import com.group4.ecommerceplatform.responses.admin.ProductSearchResponse;
import com.group4.ecommerceplatform.services.admin.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping
    public String getProductListPage(Model model, @RequestParam(value = "page", required = false, defaultValue = "1") String page, @RequestParam(value = "size", required = false, defaultValue = "10") String size){
        int pageNumber= Integer.parseInt(page);
        int pageSize = (Integer.parseInt(size));
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageDataResponse<ProductSearchResponse> result = productService.getProductList(pageable);
        model.addAttribute("pagination", result.getMetaPagination());
        model.addAttribute("productList", result.getData());
        return "admin/pages/product-list";
    }
    @GetMapping("/create")
    public String getProductCreatePage(Model model){
        return "admin/pages/product-create";
    }

    @GetMapping("/detail/{id}")
    public String getProductUpdatePage(Model model, @PathVariable("id") Long id){
        ProductDTO productDTO = productService.getProductById(id);
        model.addAttribute("product",productDTO);
        return "admin/pages/product-detail";
    }
}
