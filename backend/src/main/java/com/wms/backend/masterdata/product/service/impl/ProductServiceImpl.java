package com.wms.backend.masterdata.product.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.masterdata.product.dto.ProductDTO;
import com.wms.backend.masterdata.product.entity.Product;
import com.wms.backend.masterdata.product.mapper.ProductMapper;
import com.wms.backend.masterdata.product.repository.CategoryRepository;
import com.wms.backend.masterdata.product.repository.ProductRepository;
import com.wms.backend.masterdata.product.service.ProductService;
import com.wms.backend.shared.exception.ConflictException;
import com.wms.backend.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final AuditLogService auditLogService;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating productDTO: {}", productDTO.getProductCode());

        if (productDTO.getCategoryId() != null && !categoryRepository.existsById(productDTO.getCategoryId())) {
            throw new EntityNotFoundException("Category not found", "Category", "notfound");
        }

        if (productRepository.existsByProductCode(productDTO.getProductCode())) {
            throw new ConflictException("Product code already exists: " + productDTO.getProductCode(), "Product",
                    "codeexists");
        }

        if (productDTO.getBarcode() != null && productRepository.existsByBarcode(productDTO.getBarcode())) {
            throw new ConflictException("Barcode already exists: " + productDTO.getBarcode(), "Product",
                    "barcodeexists");
        }

        if (productDTO.getRfidTag() != null && productRepository.existsByRfidTag(productDTO.getRfidTag())) {
            throw new ConflictException("RFID Tag already exists: " + productDTO.getRfidTag(), "Product", "rfidexists");
        }

        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        auditLogService.log("CREATE", "PRODUCT", product.getId(), null, productDTO);
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Getting product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found", "Product", "notfound"));
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product id: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found", "Product", "notfound"));

        if (productDTO.getCategoryId() != null && !categoryRepository.existsById(productDTO.getCategoryId())) {
            throw new EntityNotFoundException("Category not found", "Category", "notfound");
        }

        // Check Unique Constraints
        if (!existingProduct.getProductCode().equals(productDTO.getProductCode()) &&
                productRepository.existsByProductCode(productDTO.getProductCode())) {
            throw new ConflictException("Product code already exists: " + productDTO.getProductCode(), "Product",
                    "codeexists");
        }

        if (productDTO.getBarcode() != null && !productDTO.getBarcode().equals(existingProduct.getBarcode()) &&
                productRepository.existsByBarcode(productDTO.getBarcode())) {
            throw new ConflictException("Barcode already exists: " + productDTO.getBarcode(), "Product",
                    "barcodeexists");
        }

        if (productDTO.getRfidTag() != null && !productDTO.getRfidTag().equals(existingProduct.getRfidTag()) &&
                productRepository.existsByRfidTag(productDTO.getRfidTag())) {
            throw new ConflictException("RFID Tag already exists: " + productDTO.getRfidTag(), "Product", "rfidexists");
        }

        ProductDTO oldData = productMapper.toDTO(existingProduct);
        productMapper.partialUpdate(existingProduct, productDTO);
        Product updatedProduct = productRepository.save(existingProduct);

        auditLogService.log("UPDATE", "PRODUCT", id, oldData, productDTO);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product id: {}", id);

        // Check permissions
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found", "Product", "notfound");
        }
        productRepository.deleteById(id);
        auditLogService.log("DELETE", "PRODUCT", id, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Specification<Product> spec, Pageable pageable) {
        log.info("Fetching all products with filter");
        Page<Product> page = productRepository.findAll(spec, pageable);
        return page.map(productMapper::toDTO);
    }
}
