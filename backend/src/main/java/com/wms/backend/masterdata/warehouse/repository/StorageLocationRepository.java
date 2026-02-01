package com.wms.backend.masterdata.warehouse.repository;

import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageLocationRepository
        extends JpaRepository<StorageLocation, Long>, JpaSpecificationExecutor<StorageLocation> {
    boolean existsByWarehouseIdAndLocationCode(Long warehouseId, String locationCode);

    boolean existsByBarcode(String barcode);

    boolean existsByZoneId(Long zoneId);
}
