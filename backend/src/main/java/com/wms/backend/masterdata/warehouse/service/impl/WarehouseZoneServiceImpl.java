package com.wms.backend.masterdata.warehouse.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.entity.User;
import com.wms.backend.masterdata.warehouse.dto.WarehouseZoneDTO;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import com.wms.backend.masterdata.warehouse.mapper.WarehouseZoneMapper;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseZoneRepository;
import com.wms.backend.masterdata.warehouse.service.WarehouseZoneService;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseZoneServiceImpl implements WarehouseZoneService {

    private final WarehouseZoneRepository zoneRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseZoneMapper zoneMapper;
    private final AuditLogService auditLogService;
    private final com.wms.backend.auth.repository.UserRepository userRepository;
    private final com.wms.backend.masterdata.warehouse.repository.StorageLocationRepository storageLocationRepository;

    @Override
    public WarehouseZoneDTO createZone(WarehouseZoneDTO zoneDTO) {
        log.info("Creating zone: {} for warehouse: {}", zoneDTO.getZoneCode(), zoneDTO.getWarehouseId());

        if (!warehouseRepository.existsById(zoneDTO.getWarehouseId())) {
            throw new EntityNotFoundException("Warehouse not found", "Warehouse", "notfound");
        }

        if (zoneRepository.existsByWarehouseIdAndZoneCode(zoneDTO.getWarehouseId(), zoneDTO.getZoneCode())) {
            throw new ConflictException(
                    "Zone code " + zoneDTO.getZoneCode() + " already exists in warehouse " + zoneDTO.getWarehouseId(),
                    "WarehouseZone", "codeexists");
        }

        WarehouseZone zone = zoneMapper.toEntity(zoneDTO);
        zone = zoneRepository.save(zone);
        auditLogService.log("CREATE", "WAREHOUSE_ZONE", zone.getId(), null, zoneDTO);
        return zoneMapper.toDTO(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseZoneDTO getZoneById(Long id) {
        log.info("Getting zone by id: {}", id);

        WarehouseZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WarehouseZone not found", "WarehouseZone", "notfound"));

        // Check permissions
        if (!SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            String currentUserLogin = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            User currentUser = userRepository.findOneByEmail(currentUserLogin)
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            if (currentUser.getWarehouseId() == null
                    || !currentUser.getWarehouseId().equals(zone.getWarehouse().getId())) {
                throw new EntityNotFoundException("WarehouseZone not found", "WarehouseZone", "notfound");
            }
        }

        return zoneMapper.toDTO(zone);
    }

    @Override
    public WarehouseZoneDTO updateZone(Long id, WarehouseZoneDTO zoneDTO) {
        log.info("Updating zone id: {}", id);
        WarehouseZone existingZone = zoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WarehouseZone not found", "WarehouseZone", "notfound"));

        // Check duplicate code if changed
        if (!existingZone.getZoneCode().equals(zoneDTO.getZoneCode()) &&
                zoneRepository.existsByWarehouseIdAndZoneCode(existingZone.getWarehouse().getId(),
                        zoneDTO.getZoneCode())) {
            throw new ConflictException("Zone code " + zoneDTO.getZoneCode() + " already exists in this warehouse",
                    "WarehouseZone", "codeexists");
        }

        WarehouseZoneDTO oldData = zoneMapper.toDTO(existingZone);
        zoneMapper.partialUpdate(existingZone, zoneDTO);
        WarehouseZone updatedZone = zoneRepository.save(existingZone);

        auditLogService.log("UPDATE", "WAREHOUSE_ZONE", id, oldData, zoneDTO);
        return zoneMapper.toDTO(updatedZone);
    }

    @Override
    public void deleteZone(Long id) {
        log.info("Deleting zone id: {}", id);

        // Additional constraint: Must be empty.
        if (storageLocationRepository.existsByZoneId(id)) {
            throw new ConflictException("Cannot delete zone with existing locations", "WarehouseZone", "haslocations");
        }

        zoneRepository.deleteById(id);
        auditLogService.log("DELETE", "WAREHOUSE_ZONE", id, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<WarehouseZoneDTO> getAllZones(Specification<WarehouseZone> spec, Pageable pageable) {
        log.info("Fetching all zones with filter");

        if (!SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            String currentUserLogin = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            User currentUser = userRepository.findOneByEmail(currentUserLogin)
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            Long userWarehouseId = currentUser.getWarehouseId();
            if (userWarehouseId == null) {
                return PagedResponse.empty(pageable);
            }

            Specification<WarehouseZone> warehouseSpec = (root, query, cb) -> cb.equal(root.get("warehouse").get("id"),
                    userWarehouseId);
            if (spec != null) {
                spec = spec.and(warehouseSpec);
            } else {
                spec = warehouseSpec;
            }
        }

        Page<WarehouseZone> page = zoneRepository.findAll(spec, pageable);
        return new PagedResponse<>(page.map(zoneMapper::toDTO));
    }
}
