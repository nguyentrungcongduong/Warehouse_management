package com.wms.backend.auth.service;

import com.wms.backend.auth.entity.Permission;
import java.util.List;
import java.util.Optional;

public interface PermissionService {
    Optional<Permission> findByCode(String code);

    Permission save(Permission permission);

    List<Permission> findAll();

    void delete(Long id);

    com.wms.backend.auth.entity.Permission createPermission(
            com.wms.backend.auth.dto.request.CreatePermissionRequest request);

    com.wms.backend.auth.entity.Permission findById(Long id);

    com.wms.backend.shared.dto.response.PagedResponse<com.wms.backend.auth.entity.Permission> findAll(
            org.springframework.data.jpa.domain.Specification<Permission> spec,
            org.springframework.data.domain.Pageable pageable);

    com.wms.backend.auth.entity.Permission updatePartialPermission(
            com.wms.backend.auth.dto.request.UpdatePermissionRequest request);

    void deleteById(Long id);
}
