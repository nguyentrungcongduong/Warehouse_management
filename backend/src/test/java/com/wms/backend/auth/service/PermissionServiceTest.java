package com.wms.backend.auth.service;

import com.wms.backend.auth.dto.request.CreatePermissionRequest;
import com.wms.backend.auth.dto.request.UpdatePermissionRequest;
import com.wms.backend.auth.entity.Permission;
import com.wms.backend.auth.mapper.PermissionMapper;
import com.wms.backend.auth.repository.PermissionRepository;
import com.wms.backend.auth.repository.RoleRepository;
import com.wms.backend.auth.service.impl.PermissionServiceImpl;
import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;
    private CreatePermissionRequest createPermissionRequest;

    @BeforeEach
    public void setup() {
        permission = Permission.builder()
                .id(1L)
                .code("USER_CREATE")
                .name("Create User")
                .module("User")
                .build();

        createPermissionRequest = new CreatePermissionRequest();
        createPermissionRequest.setCode("USER_CREATE");
        createPermissionRequest.setName("Create User");
    }

    @Test
    public void givenNewPermission_whenCreate_thenSaved() {
        Permission newPermission = Permission.builder()
                .code("USER_CREATE")
                .name("Create User")
                .module("User")
                .build();

        when(permissionRepository.existsByCode("USER_CREATE")).thenReturn(false);
        when(permissionMapper.toEntity(any(CreatePermissionRequest.class))).thenReturn(newPermission);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        Permission created = permissionService.createPermission(createPermissionRequest);

        assertThat(created).isNotNull();
        assertThat(created.getCode()).isEqualTo("USER_CREATE");
        verify(auditLogService, times(1)).log(eq("CREATE"), eq("Permission"), eq(1L), eq(null), any(Permission.class));
    }

    @Test
    public void givenExistingPermissionCode_whenCreate_thenThrowsConflict() {
        when(permissionRepository.existsByCode("USER_CREATE")).thenReturn(true);

        assertThrows(ConflictException.class, () -> permissionService.createPermission(createPermissionRequest));
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    public void givenExistingPermission_whenUpdatePartial_thenUpdated() {
        UpdatePermissionRequest updateRequest = new UpdatePermissionRequest();
        updateRequest.setId(1L);
        updateRequest.setCode("USER_UPDATE");

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(permissionRepository.existsByCodeAndIdNot("USER_UPDATE", 1L)).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        permissionService.updatePartialPermission(updateRequest);

        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    public void givenPermissionInUse_whenDelete_thenThrowsConflictException() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(roleRepository.existsByPermissionsId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> permissionService.delete(1L));
        verify(permissionRepository, never()).delete(any(Permission.class));
    }

    @Test
    public void givenPermissionNotInUse_whenDelete_thenDeletes() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(roleRepository.existsByPermissionsId(1L)).thenReturn(false);

        permissionService.delete(1L);

        verify(permissionRepository).delete(permission);
        verify(auditLogService).log(eq("DELETE"), eq("Permission"), eq(1L), eq(permission), eq(null));
    }
}
