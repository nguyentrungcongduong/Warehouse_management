package com.wms.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.auth.dto.request.CreatePermissionRequest;
import com.wms.backend.auth.entity.Permission;
import com.wms.backend.auth.service.PermissionService;
import com.wms.backend.shared.dto.response.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PermissionController.class)
@AutoConfigureMockMvc // Security filters enabled by default
@org.springframework.context.annotation.Import(com.wms.backend.auth.config.TestSecurityConfig.class)
public class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService permissionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Permission permission;

    @BeforeEach
    public void setup() {
        permission = Permission.builder()
                .id(1L)
                .code("USER_READ")
                .name("Read User")
                .module("User")
                .build();
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    public void givenPermission_whenCreatePermission_thenReturnsCreatedPermission() throws Exception {
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setCode("USER_READ");
        request.setName("Read User");
        request.setModule("User");

        when(permissionService.createPermission(any(CreatePermissionRequest.class))).thenReturn(permission);

        mockMvc.perform(post("/api/v1/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("USER_READ"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    public void givenPermissions_whenGetAllPermissions_thenReturnsPagedResponse() throws Exception {
        Page<Permission> page = new PageImpl<>(List.of(permission));
        PagedResponse<Permission> pagedResponse = new PagedResponse<>(page);

        when(permissionService.findAll(any(), any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/permissions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].code").value("USER_READ"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    public void givenPermissionId_whenGetPermissionById_thenReturnsPermission() throws Exception {
        when(permissionService.findById(1L)).thenReturn(permission);

        mockMvc.perform(get("/api/v1/permissions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("USER_READ"));
    }

    @Test
    @WithMockUser(authorities = "WRONG_AUTHORITY")
    public void givenWrongAuthority_whenCreatePermission_thenReturnsForbidden() throws Exception {
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setCode("USER_READ");
        request.setName("Read User");
        request.setModule("User");

        mockMvc.perform(post("/api/v1/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
