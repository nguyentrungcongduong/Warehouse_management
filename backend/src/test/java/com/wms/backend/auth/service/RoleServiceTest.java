package com.wms.backend.auth.service;

import com.wms.backend.auth.dto.request.CreateRoleRequest;
import com.wms.backend.auth.dto.request.UpdateRoleRequest;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.mapper.RoleMapper;
import com.wms.backend.auth.repository.RoleRepository;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.auth.service.impl.RoleServiceImpl;
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
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private CreateRoleRequest createRoleRequest;

    @BeforeEach
    public void setup() {
        role = Role.builder()
                .id(1L)
                .name("ROLE_ADMIN")
                .description("Admin Role")
                .active(true)
                .build();

        createRoleRequest = new CreateRoleRequest();
        createRoleRequest.setName("ROLE_ADMIN");
        createRoleRequest.setDescription("Admin Role");
    }

    @Test
    public void givenNewRole_whenCreateRole_thenSaved() {
        Role newRole = Role.builder()
                .name("ROLE_ADMIN")
                .description("Admin Role")
                .active(true)
                .build();

        when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(false);
        when(roleMapper.toEntity(any(CreateRoleRequest.class))).thenReturn(newRole);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role createdRole = roleService.createRole(createRoleRequest);

        assertThat(createdRole).isNotNull();
        assertThat(createdRole.getName()).isEqualTo("ROLE_ADMIN");
        verify(auditLogService, times(1)).log(eq("CREATE"), eq("Role"), eq(1L), eq(null), any(Role.class));
    }

    @Test
    public void givenExistingRoleName_whenCreateRole_thenThrowsConflict() {
        when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(true);

        assertThrows(ConflictException.class, () -> roleService.createRole(createRoleRequest));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void givenExistingRole_whenUpdatePartial_thenUpdated() {
        UpdateRoleRequest updateRequest = new UpdateRoleRequest();
        updateRequest.setId(1L);
        updateRequest.setName("ROLE_SUPER_ADMIN");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByNameAndIdNot("ROLE_SUPER_ADMIN", 1L)).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        roleService.updatePartialRole(updateRequest);

        verify(roleMapper).updateEntityFromRequest(eq(updateRequest), eq(role));
        verify(roleRepository).save(role);
    }

    @Test
    public void givenRoleInUse_whenDelete_thenThrowsConflictException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.existsByRolesId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> roleService.delete(1L));
        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    public void givenRoleNotUse_whenDelete_thenDeletes() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.existsByRolesId(1L)).thenReturn(false);

        roleService.delete(1L);

        verify(roleRepository).delete(role);
        verify(auditLogService).log(eq("DELETE"), eq("Role"), eq(1L), eq(role), eq(null));
    }
}
