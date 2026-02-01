package com.wms.backend.masterdata.warehouse.repository;

import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseZoneRepository
        extends JpaRepository<WarehouseZone, Long>, JpaSpecificationExecutor<WarehouseZone> {
    Optional<WarehouseZone> findByWarehouseIdAndZoneCode(Long warehouseId, String zoneCode);

    boolean existsByWarehouseIdAndZoneCode(Long warehouseId, String zoneCode);
}
