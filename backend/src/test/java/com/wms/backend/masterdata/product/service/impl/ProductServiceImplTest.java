package com.wms.backend.masterdata.product.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.masterdata.product.dto.ProductDTO;
import com.wms.backend.masterdata.product.entity.Product;
import com.wms.backend.masterdata.product.mapper.ProductMapper;
import com.wms.backend.masterdata.product.repository.CategoryRepository;
import com.wms.backend.masterdata.product.repository.ProductRepository;
import com.wms.backend.shared.exception.ConflictException;
import com.wms.backend.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_Success() {
        ProductDTO dto = new ProductDTO();
        dto.setProductCode("P001");
        dto.setCategoryId(1L);

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsByProductCode("P001")).thenReturn(false);

        Product product = new Product();
        product.setId(10L);
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(dto);

        ProductDTO result = productService.createProduct(dto);

        assertNotNull(result);
        verify(auditLogService).log(eq("CREATE"), eq("PRODUCT"), eq(10L), any(), eq(dto));
    }

    @Test
    void createProduct_CategoryNotFound() {
        ProductDTO dto = new ProductDTO();
        dto.setCategoryId(99L);
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> productService.createProduct(dto));
    }

    @Test
    void createProduct_Conflict_ProductCode() {
        ProductDTO dto = new ProductDTO();
        dto.setProductCode("P001");
        dto.setCategoryId(1L);

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsByProductCode("P001")).thenReturn(true);

        assertThrows(ConflictException.class, () -> productService.createProduct(dto));
    }
}
