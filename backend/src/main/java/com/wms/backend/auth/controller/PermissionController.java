package com.wms.backend.auth.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wms.backend.auth.dto.request.CreatePermissionRequest;
import com.wms.backend.auth.dto.request.UpdatePermissionRequest;
import com.wms.backend.auth.entity.Permission;
import com.wms.backend.auth.service.PermissionService;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.wms.backend.shared.util.anotation.ApiMessage;
import com.wms.backend.shared.exception.BadRequestAlertException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Permission", description = "Permission management")
public class PermissionController {

    private static final String ENTITY_NAME = "Permission";

    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Permission created successfully")
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        log.info("REST request to create Permission: {}", request);
        Permission createdPermission = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPermission);
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Get permission successfully")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") Long id) {
        log.info("REST request to get Permission by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(permissionService.findById(id));
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions successfully")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<PagedResponse<Permission>> getAllPermissions(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        log.info("REST request to get all Permissions, pageable: {}", pageable);
        PagedResponse<Permission> result = permissionService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/permissions/{id}")
    @ApiMessage("Permission updated successfully")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ResponseEntity<Permission> updatePartialPermission(@PathVariable("id") Long id,
            @Valid @RequestBody UpdatePermissionRequest permission) {
        log.info("REST request to update Permission partially, id: {}, body: {}", id, permission);
        if (permission.getId() <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be invalid", ENTITY_NAME, "invalidpermission");
        }
        if (!id.equals(permission.getId())) {
            throw new BadRequestAlertException("Permission ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        Permission updatedPermission = permissionService.updatePartialPermission(permission);
        return ResponseEntity.ok().body(updatedPermission);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Xóa permission thành công")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ResponseEntity<Map<String, String>> deletePermissionById(@PathVariable("id") Long id) {
        log.info("REST request to delete Permission by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        permissionService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }
}