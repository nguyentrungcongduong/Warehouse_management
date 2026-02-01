package com.wms.backend.masterdata.warehouse.repository;

import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class WarehouseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    void existsByWarehouseCode_ShouldReturnTrue_WhenExists() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH_TEST");
        warehouse.setWarehouseName("Test Warehouse");
        entityManager.persist(warehouse);
        entityManager.flush();

        boolean exists = warehouseRepository.existsByWarehouseCode("WH_TEST");
        assertTrue(exists);
    }

    @Test
    void existsByWarehouseCode_ShouldReturnFalse_WhenNotExists() {
        boolean exists = warehouseRepository.existsByWarehouseCode("WH_NON_EXISTENT");
        assertFalse(exists);
    }
}
