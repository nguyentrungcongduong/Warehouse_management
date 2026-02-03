package com.wms.backend.masterdata.warehouse.repository;

import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WarehouseZoneRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WarehouseZoneRepository zoneRepository;

    @Test
    void findByWarehouseIdAndZoneCode_ShouldReturnZone_WhenExists() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH_Z_TEST");
        warehouse.setWarehouseName("Test Warehouse Zone");
        warehouse = entityManager.persist(warehouse);

        WarehouseZone zone = new WarehouseZone();
        zone.setZoneCode("Z01");
        zone.setZoneName("Zone 1");
        zone.setWarehouse(warehouse);
        entityManager.persist(zone);
        entityManager.flush();

        Optional<WarehouseZone> found = zoneRepository.findByWarehouseIdAndZoneCode(warehouse.getId(), "Z01");
        assertTrue(found.isPresent());
        assertEquals("Z01", found.get().getZoneCode());
    }

    @Test
    void existsByWarehouseIdAndZoneCode_ShouldReturnTrue_WhenExists() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH_Z_EXISTS");
        warehouse.setWarehouseName("Test Warehouse Exists");
        warehouse = entityManager.persist(warehouse);

        WarehouseZone zone = new WarehouseZone();
        zone.setZoneCode("Z_EXISTS");
        zone.setZoneName("Zone Exists");
        zone.setWarehouse(warehouse);
        entityManager.persist(zone);
        entityManager.flush();

        boolean exists = zoneRepository.existsByWarehouseIdAndZoneCode(warehouse.getId(), "Z_EXISTS");
        assertTrue(exists);
    }
}
