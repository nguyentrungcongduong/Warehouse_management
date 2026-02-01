package com.wms.backend.masterdata.warehouse.service;

import com.wms.backend.masterdata.warehouse.dto.WarehouseZoneDTO;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface WarehouseZoneService {
    WarehouseZoneDTO createZone(WarehouseZoneDTO zoneDTO);

    WarehouseZoneDTO getZoneById(Long id);

    WarehouseZoneDTO updateZone(Long id, WarehouseZoneDTO zoneDTO);

    void deleteZone(Long id);

    Page<WarehouseZoneDTO> getAllZones(Specification<WarehouseZone> spec, Pageable pageable);
}
