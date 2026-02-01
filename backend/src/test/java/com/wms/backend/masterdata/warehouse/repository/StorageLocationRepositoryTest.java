package com.wms.backend.masterdata.warehouse.repository;

import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class StorageLocationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StorageLocationRepository locationRepository;

    @Test
    void existsByWarehouseIdAndLocationCode_ShouldReturnTrue_WhenExists() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH_LOC_TEST");
        warehouse.setWarehouseName("Test Warehouse Location");
        warehouse = entityManager.persist(warehouse);

        WarehouseZone zone = new WarehouseZone();
        zone.setZoneCode("Z_LOC");
        zone.setWarehouse(warehouse);
        zone = entityManager.persist(zone);

        StorageLocation location = new StorageLocation();
        location.setLocationCode("LOC001");
        location.setWarehouse(warehouse);
        location.setZone(zone);
        entityManager.persist(location);
        entityManager.flush();

        boolean exists = locationRepository.existsByWarehouseIdAndLocationCode(warehouse.getId(), "LOC001");
        assertTrue(exists);
    }

    @Test
    void existsByBarcode_ShouldReturnTrue_WhenExists() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH_BAR_TEST");
        warehouse.setWarehouseName("Test Warehouse Barcode");
        warehouse = entityManager.persist(warehouse);

        WarehouseZone zone = new WarehouseZone();
        zone.setZoneCode("Z_BAR");
        zone.setWarehouse(warehouse);
        zone = entityManager.persist(zone);

        StorageLocation location = new StorageLocation();
        location.setLocationCode("LOC002");
        location.setBarcode("BAR_LOC_002");
        location.setWarehouse(warehouse);
        location.setZone(zone);
        entityManager.persist(location);
        entityManager.flush();

        boolean exists = locationRepository.existsByBarcode("BAR_LOC_002");
        assertTrue(exists);
    }
}
