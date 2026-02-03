package com.wms.backend.masterdata.warehouse.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.entity.User;
import com.wms.backend.masterdata.warehouse.dto.WarehouseDTO;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.mapper.WarehouseMapper;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import com.wms.backend.masterdata.warehouse.service.WarehouseService;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.ConflictException;
import com.wms.backend.shared.exception.EntityNotFoundException;
import com.wms.backend.shared.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AuditLogService auditLogService;
    private final com.wms.backend.auth.repository.UserRepository userRepository;

    @Override
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        log.info("Creating warehouse: {}", warehouseDTO.getWarehouseCode());

        if (warehouseRepository.existsByWarehouseCode(warehouseDTO.getWarehouseCode())) {
            throw new ConflictException("Warehouse code already exists: " + warehouseDTO.getWarehouseCode(),
                    "Warehouse", "codeexists");
        }
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        auditLogService.log("CREATE", "WAREHOUSE", warehouse.getId(), null, warehouseDTO);
        return warehouseMapper.toDTO(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseById(Long id) {
        log.info("Getting warehouse by id: {}", id);

        // Check permissions: Data Scoping
        if (!SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            String currentUserLogin = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            User currentUser = userRepository.findByEmail(currentUserLogin)
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            if (currentUser.getWarehouseId() == null || !currentUser.getWarehouseId().equals(id)) {
                // Return Not Found if outside scope
                throw new EntityNotFoundException("Warehouse not found", "Warehouse", "notfound");
            }
        }

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found", "Warehouse", "notfound"));
        return warehouseMapper.toDTO(warehouse);
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO) {
        log.info("Updating warehouse id: {}", id);

        Warehouse existingWarehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found", "Warehouse", "notfound"));

        // Check if code is changing and if it conflicts
        if (!existingWarehouse.getWarehouseCode().equals(warehouseDTO.getWarehouseCode()) &&
                warehouseRepository.existsByWarehouseCode(warehouseDTO.getWarehouseCode())) {
            throw new ConflictException("Warehouse code already exists: " + warehouseDTO.getWarehouseCode(),
                    "Warehouse", "codeexists");
        }

        WarehouseDTO oldData = warehouseMapper.toDTO(existingWarehouse);
        warehouseMapper.partialUpdate(existingWarehouse, warehouseDTO);
        Warehouse updatedWarehouse = warehouseRepository.save(existingWarehouse);

        auditLogService.log("UPDATE", "WAREHOUSE", id, oldData, warehouseDTO);
        return warehouseMapper.toDTO(updatedWarehouse);
    }

    @Override
    public void deleteWarehouse(Long id) {
        log.info("Deleting warehouse id: {}", id);

        if (!warehouseRepository.existsById(id)) {
            throw new EntityNotFoundException("Warehouse not found", "Warehouse", "notfound");
        }
        warehouseRepository.deleteById(id);
        auditLogService.log("DELETE", "WAREHOUSE", id, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<WarehouseDTO> getAllWarehouses(Specification<Warehouse> spec, Pageable pageable) {
        log.info("Fetching all warehouses with filter");

        if (!SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            String currentUserLogin = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            User currentUser = userRepository.findByEmail(currentUserLogin)
                    .orElseThrow(() -> new EntityNotFoundException("User not found", "User", "notfound"));

            Long userWarehouseId = currentUser.getWarehouseId();
            if (userWarehouseId == null) {
                // Return empty page if user has no warehouse assigned and is not admin
                return PagedResponse.empty(pageable);
            }

            Specification<Warehouse> warehouseSpec = (root, query, cb) -> cb.equal(root.get("id"), userWarehouseId);
            if (spec != null) {
                spec = spec.and(warehouseSpec);
            } else {
                spec = warehouseSpec;
            }
        }

        Page<Warehouse> page = warehouseRepository.findAll(spec, pageable);
        return new PagedResponse<>(page.map(warehouseMapper::toDTO));
    }
}
