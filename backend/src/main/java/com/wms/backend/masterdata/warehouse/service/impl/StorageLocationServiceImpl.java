package com.wms.backend.masterdata.warehouse.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.entity.User;
import com.wms.backend.masterdata.warehouse.dto.StorageLocationDTO;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.masterdata.warehouse.mapper.StorageLocationMapper;
import com.wms.backend.masterdata.warehouse.repository.StorageLocationRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseZoneRepository;
import com.wms.backend.masterdata.warehouse.service.StorageLocationService;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.ConflictException;
import com.wms.backend.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wms.backend.shared.util.SecurityUtil;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StorageLocationServiceImpl implements StorageLocationService {

    private final StorageLocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseZoneRepository zoneRepository;
    private final StorageLocationMapper locationMapper;
    private final AuditLogService auditLogService;
    private final com.wms.backend.auth.repository.UserRepository userRepository;

    @Override
    public StorageLocationDTO createLocation(StorageLocationDTO locationDTO) {
        log.info("Creating location: {} for warehouse: {}", locationDTO.getLocationCode(),
                locationDTO.getWarehouseId());

        if (!warehouseRepository.existsById(locationDTO.getWarehouseId())) {
            throw new EntityNotFoundException("Warehouse not found", "Warehouse", "notfound");
        }

        if (!zoneRepository.existsById(locationDTO.getZoneId())) {
            throw new EntityNotFoundException("WarehouseZone not found", "WarehouseZone", "notfound");
        }

        if (locationRepository.existsByWarehouseIdAndLocationCode(locationDTO.getWarehouseId(),
                locationDTO.getLocationCode())) {
            throw new ConflictException(
                    "Location code " + locationDTO.getLocationCode() + " already exists in this warehouse",
                    "StorageLocation", "codeexists");
        }

        if (locationDTO.getBarcode() != null && locationRepository.existsByBarcode(locationDTO.getBarcode())) {
            throw new ConflictException("Barcode " + locationDTO.getBarcode() + " already exists", "StorageLocation",
                    "barcodeexists");
        }

        StorageLocation location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        auditLogService.log("CREATE", "STORAGE_LOCATION", location.getId(), null, locationDTO);
        return locationMapper.toDTO(location);
    }

    @Override
    @Transactional(readOnly = true)
    public StorageLocationDTO getLocationById(Long id) {
        log.info("Getting location by id: {}", id);
        StorageLocation location = locationRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("StorageLocation not found", "StorageLocation", "notfound"));

        // Check permissions
        if (!SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            String currentUserLogin = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            User currentUser = userRepository.findByEmail(currentUserLogin)
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            if (currentUser.getWarehouseId() == null
                    || !currentUser.getWarehouseId().equals(location.getWarehouse().getId())) {
                throw new EntityNotFoundException("StorageLocation not found", "StorageLocation", "notfound");
            }
        }

        return locationMapper.toDTO(location);
    }

    @Override
    public StorageLocationDTO updateLocation(Long id, StorageLocationDTO locationDTO) {
        log.info("Updating location id: {}", id);
        StorageLocation existingLocation = locationRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("StorageLocation not found", "StorageLocation", "notfound"));

        // Check duplicate code if changed
        if (!existingLocation.getLocationCode().equals(locationDTO.getLocationCode()) &&
                locationRepository.existsByWarehouseIdAndLocationCode(existingLocation.getWarehouse().getId(),
                        locationDTO.getLocationCode())) {
            throw new ConflictException(
                    "Location code " + locationDTO.getLocationCode() + " already exists in this warehouse",
                    "StorageLocation", "codeexists");
        }

        // Check duplicate barcode if changed
        if (locationDTO.getBarcode() != null && !locationDTO.getBarcode().equals(existingLocation.getBarcode()) &&
                locationRepository.existsByBarcode(locationDTO.getBarcode())) {
            throw new ConflictException("Barcode " + locationDTO.getBarcode() + " already exists", "StorageLocation",
                    "barcodeexists");
        }

        StorageLocationDTO oldData = locationMapper.toDTO(existingLocation);
        locationMapper.partialUpdate(existingLocation, locationDTO);
        StorageLocation updatedLocation = locationRepository.save(existingLocation);

        auditLogService.log("UPDATE", "STORAGE_LOCATION", id, oldData, locationDTO);
        return locationMapper.toDTO(updatedLocation);
    }

    @Override
    public void deleteLocation(Long id) {
        log.info("Deleting location id: {}", id);

        StorageLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StorageLocation not found", "StorageLocation",
                        "notfound"));

        // Additional constraint: Must be empty.
        if (location.getCurrentCapacity() != null && location.getCurrentCapacity().compareTo(BigDecimal.ZERO) > 0) {
            throw new ConflictException("Cannot delete location that is not empty", "StorageLocation", "notempty");
        }

        locationRepository.deleteById(id);
        auditLogService.log("DELETE", "STORAGE_LOCATION", id, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StorageLocationDTO> getAllLocations(Specification<StorageLocation> spec, Pageable pageable) {
        log.info("Fetching all locations with filter");

        if (!SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            String currentUserLogin = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            User currentUser = userRepository.findByEmail(currentUserLogin)
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            Long userWarehouseId = currentUser.getWarehouseId();
            if (userWarehouseId == null) {
                return PagedResponse.empty(pageable);
            }

            Specification<StorageLocation> warehouseSpec = (root, query, cb) -> cb
                    .equal(root.get("warehouse").get("id"), userWarehouseId);
            if (spec != null) {
                spec = spec.and(warehouseSpec);
            } else {
                spec = warehouseSpec;
            }
        }

        Page<StorageLocation> page = locationRepository.findAll(spec, pageable);
        return new PagedResponse<>(page.map(locationMapper::toDTO));
    }
}
