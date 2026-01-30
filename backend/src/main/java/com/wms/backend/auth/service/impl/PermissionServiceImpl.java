package com.wms.backend.auth.service.impl;

import com.wms.backend.auth.dto.request.CreatePermissionRequest;
import com.wms.backend.auth.dto.request.UpdatePermissionRequest;
import com.wms.backend.auth.entity.Permission;
import com.wms.backend.auth.repository.PermissionRepository;
import com.wms.backend.auth.service.PermissionService;
import com.wms.backend.shared.dto.response.PagedResponse;

import com.wms.backend.auth.mapper.PermissionMapper;
import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.repository.RoleRepository;
import com.wms.backend.shared.exception.ConflictException;
import com.wms.backend.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findByCode(String code) {
        log.debug("Finding permission by code: {}", code);
        return permissionRepository.findByCode(code);
    }

    @Override
    public Permission save(Permission permission) {
        log.debug("Saving permission: {}", permission);
        boolean isNew = permission.getId() == null;
        Permission oldPermission = null;
        if (!isNew) {
            oldPermission = permissionRepository.findById(permission.getId()).orElse(null);
        }

        Permission savedPermission = permissionRepository.save(permission);

        if (isNew) {
            auditLogService.log("CREATE", "Permission", savedPermission.getId(), null, savedPermission);
        } else if (oldPermission != null) {
            auditLogService.log("UPDATE", "Permission", savedPermission.getId(), oldPermission, savedPermission);
        }

        return savedPermission;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAll() {
        log.debug("Finding all permissions");
        return permissionRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting permission by id: {}", id);
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found for deletion", "Permission",
                        "notfound"));

        // Check availability
        if (roleRepository.existsByPermissionsId(id)) {
            throw new ConflictException("Permission is currently in use by roles and cannot be deleted", "Permission",
                    "inuse");
        }

        permissionRepository.delete(permission);
        auditLogService.log("DELETE", "Permission", permission.getId(), permission, null);
    }

    @Override
    public Permission createPermission(CreatePermissionRequest request) {
        log.info("Request to create permission: {}", request.getCode());

        // Check duplicate
        if (permissionRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Permission code already exists", "Permission", "exists");
        }

        Permission permission = permissionMapper.toEntity(request);
        return this.save(permission);
    }

    @Override
    public Permission findById(Long id) {
        log.info("Request to find permission by id: {}", id);
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found", "Permission", "notfound"));
    }

    @Override
    public PagedResponse<Permission> findAll(Specification<Permission> spec, Pageable pageable) {
        log.info("Request to find all permissions");
        Page<Permission> page = permissionRepository.findAll(spec, pageable);
        return new PagedResponse<>(page);
    }

    @Override
    public Permission updatePartialPermission(UpdatePermissionRequest request) {
        log.info("Request to update partial permission: {}", request.getId());
        Permission permission = this.findById(request.getId());

        // Check duplicate validation for partial update
        if (request.getCode() != null && !request.getCode().equals(permission.getCode())) {
            if (permissionRepository.existsByCodeAndIdNot(request.getCode(), request.getId())) {
                throw new ConflictException("Permission code already exists", "Permission", "exists");
            }
        }

        if (request.getName() != null) {
            permission.setName(request.getName());
        }
        if (request.getCode() != null) {
            permission.setCode(request.getCode());
        }
        if (request.getModule() != null) {
            permission.setModule(request.getModule());
        }
        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }

        return this.save(permission);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Request to delete permission by id: {}", id);
        this.delete(id);
    }
}
