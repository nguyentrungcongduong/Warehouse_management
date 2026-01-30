package com.wms.backend.auth.service;

import com.wms.backend.auth.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(String name);

    Role save(Role role);

    List<Role> findAll();

    void delete(Long id);

    com.wms.backend.auth.entity.Role createRole(com.wms.backend.auth.dto.request.CreateRoleRequest request);

    com.wms.backend.auth.entity.Role addPermissionForRole(com.wms.backend.auth.dto.request.UpdateRoleRequest request);

    com.wms.backend.auth.entity.Role findById(Long id);

    com.wms.backend.shared.dto.response.PagedResponse<com.wms.backend.auth.entity.Role> findAll(
            org.springframework.data.jpa.domain.Specification<Role> spec,
            org.springframework.data.domain.Pageable pageable);

    com.wms.backend.auth.entity.Role updatePartialRole(com.wms.backend.auth.dto.request.UpdateRoleRequest request);

    com.wms.backend.auth.dto.response.RoleResponse convertToResRoleDTO(com.wms.backend.auth.entity.Role role);

    void deleteById(Long id);

    void deletePermissionFromRole(com.wms.backend.auth.dto.request.UpdateRoleRequest request);
}
