package com.wms.backend.auth.service;

import com.wms.backend.auth.dto.request.CreateRoleRequest;
import com.wms.backend.auth.dto.request.UpdateRoleRequest;
import com.wms.backend.auth.dto.response.RoleResponse;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.shared.dto.response.PagedResponse;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleService {
    Optional<Role> findByName(String name);

    Role save(Role role);

    List<Role> findAll();

    void delete(Long id);

    Role createRole(CreateRoleRequest request);

    Role addPermissionForRole(UpdateRoleRequest request);

    Role findById(Long id);

    PagedResponse<Role> findAll(Specification<Role> spec, Pageable pageable);

    Role updatePartialRole(UpdateRoleRequest request);

    RoleResponse convertToResRoleDTO(Role role);

    void deleteById(Long id);

    void deletePermissionFromRole(UpdateRoleRequest request);
}
