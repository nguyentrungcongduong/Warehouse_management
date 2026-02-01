package com.wms.backend.masterdata.product.repository;

import com.wms.backend.masterdata.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void existsByProductCode_ShouldReturnTrue_WhenExists() {
        Product product = new Product();
        product.setProductCode("P001");
        product.setProductName("Test Product");
        entityManager.persist(product);
        entityManager.flush();

        boolean exists = productRepository.existsByProductCode("P001");
        assertTrue(exists);
    }

    @Test
    void existsByBarcode_ShouldReturnTrue_WhenExists() {
        Product product = new Product();
        product.setProductCode("P002");
        product.setProductName("Test Product 2");
        product.setBarcode("BAR002");
        entityManager.persist(product);
        entityManager.flush();

        boolean exists = productRepository.existsByBarcode("BAR002");
        assertTrue(exists);
    }
}
