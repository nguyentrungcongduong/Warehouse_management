package com.wms.backend.masterdata.product.service;

import com.wms.backend.masterdata.product.dto.ProductDTO;
import com.wms.backend.masterdata.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO getProductById(Long id);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);

    Page<ProductDTO> getAllProducts(Specification<Product> spec, Pageable pageable);
}
