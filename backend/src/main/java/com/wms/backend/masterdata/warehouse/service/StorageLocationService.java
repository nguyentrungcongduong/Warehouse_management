package com.wms.backend.masterdata.warehouse.service;

import com.wms.backend.masterdata.warehouse.dto.StorageLocationDTO;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.shared.dto.response.PagedResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StorageLocationService {
    StorageLocationDTO createLocation(StorageLocationDTO locationDTO);

    StorageLocationDTO getLocationById(Long id);

    StorageLocationDTO updateLocation(Long id, StorageLocationDTO locationDTO);

    void deleteLocation(Long id);

    PagedResponse<StorageLocationDTO> getAllLocations(Specification<StorageLocation> spec, Pageable pageable);
}
