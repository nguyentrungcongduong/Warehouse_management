package com.wms.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.auth.dto.request.CreateRoleRequest;
import com.wms.backend.auth.dto.response.RoleResponse;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.service.RoleService;
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

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc // Security filters enabled by default
@org.springframework.context.annotation.Import(com.wms.backend.auth.config.TestSecurityConfig.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role;
    private RoleResponse roleResponse;

    @BeforeEach
    public void setup() {
        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .description("Ordinary User")
                .build();

        roleResponse = new RoleResponse();
        roleResponse.setId(1L);
        roleResponse.setName("ROLE_USER");
    }

    @Test
    @WithMockUser(authorities = "ROLE_CREATE")
    public void givenRole_whenCreateRole_thenReturnsCreatedRole() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("ROLE_USER");
        request.setDescription("Ordinary User");

        when(roleService.createRole(any(CreateRoleRequest.class))).thenReturn(role);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_READ")
    public void givenRoles_whenGetAllRoles_thenReturnsPagedResponse() throws Exception {
        Page<Role> page = new PageImpl<>(List.of(role));
        PagedResponse<Role> pagedResponse = new PagedResponse<>(page);

        when(roleService.findAll(any(), any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_READ")
    public void givenRoleId_whenGetRoleById_thenReturnsRole() throws Exception {
        when(roleService.findById(1L)).thenReturn(role);
        when(roleService.convertToResRoleDTO(role)).thenReturn(roleResponse);

        mockMvc.perform(get("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(authorities = "WRONG_AUTHORITY")
    public void givenWrongAuthority_whenCreateRole_thenReturnsForbidden() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("ROLE_USER");
        request.setDescription("Ordinary User");

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
