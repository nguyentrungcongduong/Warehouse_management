package com.wms.backend.masterdata.product.controller;

import com.turkraft.springfilter.boot.Filter;
import com.wms.backend.masterdata.product.dto.ProductDTO;
import com.wms.backend.masterdata.product.entity.Product;
import com.wms.backend.masterdata.product.service.ProductService;
import com.wms.backend.shared.util.anotation.ApiMessage;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product Management APIs")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/warehouse/products")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Create a new product")
    @ApiMessage("Create product successfully")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        if (productDTO.getId() != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", "Product", "idexists");
        }
        ProductDTO result = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/warehouse/products/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get product by ID")
    @ApiMessage("Get product success")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Product", "idinvalid");
        }
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/warehouse/products/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Update product")
    @ApiMessage("Update product success")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Product", "idinvalid");
        }
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/warehouse/products/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @Operation(summary = "Delete product")
    @ApiMessage("Delete product success")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Product", "idinvalid");
        }
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse/products")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get all products with filter and pagination")
    @ApiMessage("Get all products success")
    public ResponseEntity<PagedResponse<ProductDTO>> getAllProducts(
            @Filter Specification<Product> spec,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(spec, pageable));
    }
}
