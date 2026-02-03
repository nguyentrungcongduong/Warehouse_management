package com.wms.backend.masterdata.warehouse.service;

import com.wms.backend.masterdata.warehouse.dto.WarehouseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.shared.dto.response.PagedResponse;

public interface WarehouseService {
    WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO);

    WarehouseDTO getWarehouseById(Long id);

    WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO);

    void deleteWarehouse(Long id);

    PagedResponse<WarehouseDTO> getAllWarehouses(Specification<Warehouse> spec, Pageable pageable);
}
