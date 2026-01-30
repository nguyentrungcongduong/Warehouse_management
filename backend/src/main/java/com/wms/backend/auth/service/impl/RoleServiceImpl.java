package com.wms.backend.auth.service.impl;

import com.wms.backend.auth.dto.request.CreateRoleRequest;
import com.wms.backend.auth.dto.request.UpdateRoleRequest;
import com.wms.backend.auth.dto.response.RoleResponse;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.repository.RoleRepository;
import com.wms.backend.auth.service.RoleService;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.auth.mapper.RoleMapper;
import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.repository.UserRepository;
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
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        log.debug("Finding role by name: {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    public Role save(Role role) {
        log.debug("Saving role: {}", role);
        boolean isNew = role.getId() == null;
        Role oldRole = null;
        if (!isNew) {
            oldRole = roleRepository.findById(role.getId()).orElse(null);
        }

        Role savedRole = roleRepository.save(role);

        if (isNew) {
            auditLogService.log("CREATE", "Role", savedRole.getId(), null, savedRole);
        } else if (oldRole != null) {
            auditLogService.log("UPDATE", "Role", savedRole.getId(), oldRole, savedRole);
        }

        return savedRole;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        log.debug("Finding all roles");
        return roleRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting role by id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found for deletion", "Role", "notfound"));

        // Check usage
        if (userRepository.existsByRolesId(id)) {
            throw new ConflictException("Role is currently assigned to users and cannot be deleted", "Role", "inuse");
        }

        roleRepository.delete(role);
        auditLogService.log("DELETE", "Role", role.getId(), role, null);
    }

    @Override
    public Role createRole(CreateRoleRequest request) {
        log.info("Request to create role: {}", request.getName());

        // Check duplicate
        if (roleRepository.existsByName(request.getName())) {
            throw new ConflictException("Role name already exists", "Role", "exists");
        }

        Role role = roleMapper.toEntity(request);
        if (role.getActive() == null) {
            role.setActive(true);
        }
        return this.save(role);
    }

    @Override
    public Role addPermissionForRole(UpdateRoleRequest request) {
        log.info("Request to add permission for role: {}", request.getId());
        Role role = this.findById(request.getId());
        roleMapper.updateEntityFromRequest(request, role);
        return this.save(role);
    }

    @Override
    public Role findById(Long id) {
        log.info("Request to find role by id: {}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found", "Role", "notfound"));
    }

    @Override
    public PagedResponse<Role> findAll(Specification<Role> spec, Pageable pageable) {
        log.info("Request to find all roles");
        Page<Role> page = roleRepository.findAll(spec, pageable);
        return new PagedResponse<>(page);
    }

    @Override
    public Role updatePartialRole(UpdateRoleRequest request) {
        log.info("Request to update partial role: {}", request.getId());
        Role role = this.findById(request.getId());

        // Check duplicate validation for partial update
        if (request.getName() != null && !request.getName().equals(role.getName())) {
            if (roleRepository.existsByNameAndIdNot(request.getName(), request.getId())) {
                throw new ConflictException("Role name already exists", "Role", "exists");
            }
        }

        roleMapper.updateEntityFromRequest(request, role);
        return this.save(role);
    }

    @Override
    public RoleResponse convertToResRoleDTO(Role role) {
        return roleMapper.toResponse(role);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Request to delete role by id: {}", id);
        this.delete(id);
    }

    @Override
    public void deletePermissionFromRole(UpdateRoleRequest request) {
        log.info("Request to delete permission from role: {}", request.getId());
        Role role = this.findById(request.getId());
        // Logic to remove permissions
        this.save(role);
    }
}
