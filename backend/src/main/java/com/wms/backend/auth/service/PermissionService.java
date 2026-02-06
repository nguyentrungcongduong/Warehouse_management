package com.wms.backend.auth.service;

import com.wms.backend.auth.dto.request.CreatePermissionRequest;
import com.wms.backend.auth.dto.request.UpdatePermissionRequest;
import com.wms.backend.auth.entity.Permission;
import com.wms.backend.shared.dto.response.PagedResponse;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PermissionService {
        Optional<Permission> findByCode(String code);

        Permission save(Permission permission);

        List<Permission> findAll();

        void delete(Long id);

        Permission createPermission(CreatePermissionRequest request);

        Permission findById(Long id);

        PagedResponse<Permission> findAll(Specification<Permission> spec, Pageable pageable);

        Permission updatePartialPermission(UpdatePermissionRequest request);

        void deleteById(Long id);
}
