package com.wms.backend.masterdata.warehouse.repository;

import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
    boolean existsByWarehouseCode(String warehouseCode);
}
