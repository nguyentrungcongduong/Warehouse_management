package com.wms.backend.masterdata.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.masterdata.product.dto.ProductDTO;
import com.wms.backend.masterdata.product.entity.Category;
import com.wms.backend.masterdata.product.entity.Product;
import com.wms.backend.masterdata.product.repository.CategoryRepository;
import com.wms.backend.masterdata.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category();
        category.setCategoryName("Electronics");
        category.setDescription("Electronic items");
        category = categoryRepository.save(category);

        product = new Product();
        product.setProductCode("P001");
        product.setProductName("Laptop");
        product.setCategory(category);
        product.setUnitOfMeasure("pcs");
        product.setWeight(new BigDecimal("1.5"));
        productRepository.save(product);
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "PRODUCT_CREATE")
    void createProduct_Success() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setProductCode("P002");
        dto.setProductName("Mouse");
        dto.setCategoryId(category.getId());
        dto.setUnitOfMeasure("pcs");

        mockMvc.perform(post("/api/v1/warehouse/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.productCode").value("P002"))
                .andExpect(jsonPath("$.data.productName").value("Mouse"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "PRODUCT_CREATE")
    void createProduct_Conflict_CodeExists() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setProductCode("P001"); // Already exists
        dto.setProductName("Duplicate Laptop");
        dto.setCategoryId(category.getId());

        mockMvc.perform(post("/api/v1/warehouse/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "PRODUCT_READ")
    void getProduct_Success() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/products/{id}", product.getId())
                .accept(MediaType.APPLICATION_JSON)) // Explicit accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productCode").value("P001"))
                .andExpect(jsonPath("$.data.productName").value("Laptop"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "PRODUCT_READ")
    void getProduct_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/products/{id}", 9999L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "PRODUCT_UPDATE")
    void updateProduct_Success() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setProductCode("P001-UPD");
        dto.setProductName("Laptop Updated");
        dto.setCategoryId(category.getId());
        dto.setUnitOfMeasure("pcs");

        mockMvc.perform(put("/api/v1/warehouse/products/{id}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productCode").value("P001-UPD"))
                .andExpect(jsonPath("$.data.productName").value("Laptop Updated"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "PRODUCT_DELETE")
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/warehouse/products/{id}", product.getId()))
                .andExpect(status().isNoContent());

        productRepository.flush();
        entityManager.clear();

        // Verify deletion
        org.junit.jupiter.api.Assertions.assertFalse(productRepository.existsById(product.getId()),
                "Product should be deleted from DB");
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "PRODUCT_READ")
    void getAllProducts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/products")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].productCode").value("P001"));
    }
}
