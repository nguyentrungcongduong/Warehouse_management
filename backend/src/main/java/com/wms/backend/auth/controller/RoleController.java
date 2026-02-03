package com.wms.backend.auth.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import com.wms.backend.auth.dto.request.CreateRoleRequest;
import com.wms.backend.auth.dto.request.UpdateRoleRequest;
import com.wms.backend.auth.dto.response.RoleResponse;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.service.RoleService;
import com.wms.backend.shared.dto.response.PagedResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.wms.backend.shared.util.anotation.ApiMessage;
import com.wms.backend.shared.exception.BadRequestAlertException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Role", description = "Role management")
public class RoleController {

    private static final String ENTITY_NAME = "Role";

    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Role created successfully")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @Operation(summary = "Create role", description = "Create a new role")
    @ApiResponse(responseCode = "201", description = "Role created successfully")
    public ResponseEntity<Role> createRole(@Valid @RequestBody CreateRoleRequest request) {
        log.info("REST request to create Role: {}", request);
        Role createdRole = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdRole);
    }

    @PostMapping("/roles/{id}/permissions")
    @ApiMessage("Permission added to role successfully")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN_PERMISSION')")
    @Operation(summary = "Add permission to role", description = "Assign permissions to a role")
    @ApiResponse(responseCode = "200", description = "Permission added to role successfully")
    public ResponseEntity<Role> addPermissionForRole(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateRoleRequest role) {
        log.info("REST request to add permission for Role: id: {}, permissions: {}", id, role.getPermissions());
        if (role.getId() <= 0) {
            throw new BadRequestAlertException("Role id cannot be invalid", ENTITY_NAME, "idnull");
        }
        if (!id.equals(role.getId())) {
            throw new BadRequestAlertException("Role ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        return ResponseEntity.ok(roleService.addPermissionForRole(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get role information successfully")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Operation(summary = "Get role by id", description = "Retrieve role details by its ID")
    @ApiResponse(responseCode = "200", description = "Get role information successfully")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable("id") Long id) {
        log.info("REST request to get Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "idnull");
        }
        return ResponseEntity.ok().body(this.roleService.convertToResRoleDTO(this.roleService.findById(id)));
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles successfully")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Operation(summary = "Get all roles", description = "Retrieve all roles with pagination and filtering")
    @ApiResponse(responseCode = "200", description = "Get all roles successfully")
    public ResponseEntity<PagedResponse<Role>> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        log.info("REST request to get all Roles, pageable: {}", pageable);
        PagedResponse<Role> result = roleService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/roles/{id}")
    @ApiMessage("Role updated successfully")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @Operation(summary = "Update role", description = "Update an existing role")
    @ApiResponse(responseCode = "200", description = "Role updated successfully")
    public ResponseEntity<RoleResponse> updatePartialRole(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateRoleRequest role) {
        log.info("REST request to update Role partially, id: {}, body: {}", id, role);
        if (role.getId() <= 0) {
            throw new BadRequestAlertException("Role invalid", ENTITY_NAME, "roleinvalid");
        }
        if (!id.equals(role.getId())) {
            throw new BadRequestAlertException("Role ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        Role updatedRole = roleService.updatePartialRole(role);
        return ResponseEntity.ok().body(this.roleService.convertToResRoleDTO(updatedRole));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Role deleted successfully")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @Operation(summary = "Delete role", description = "Delete a role by its ID")
    @ApiResponse(responseCode = "204", description = "Role deleted successfully")
    public ResponseEntity<Void> deleteRoleById(@PathVariable("id") Long id) {
        log.info("REST request to delete Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "idnull");
        }
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/roles/{id}/permissions")
    @ApiMessage("Permission removed from role successfully")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN_PERMISSION')")
    @Operation(summary = "Remove permission from role", description = "Remove permissions from a role")
    @ApiResponse(responseCode = "204", description = "Permission removed from role successfully")
    public ResponseEntity<Void> deletePermissionFromRole(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateRoleRequest role) {
        log.info("REST request to delete permission from Role by id: {}, permissions: {}", id, role.getPermissions());
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "idnull");
        }
        if (!id.equals(role.getId())) {
            throw new BadRequestAlertException("Role ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        roleService.deletePermissionFromRole(role);
        return ResponseEntity.noContent().build();
    }
}