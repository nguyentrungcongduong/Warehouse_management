package com.wms.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.auth.dto.request.CreateUserRequest;
import com.wms.backend.auth.dto.request.UpdateUserRequest;
import com.wms.backend.auth.dto.response.CreateUserResponse;
import com.wms.backend.auth.dto.response.UpdateUserResponse;
import com.wms.backend.auth.dto.response.UserResponse;
import com.wms.backend.auth.service.UserService;
import com.wms.backend.shared.dto.response.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@org.springframework.context.annotation.Import(com.wms.backend.auth.config.TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private CreateUserResponse createUserResponse;

    @BeforeEach
    public void setup() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");

        createUserResponse = new CreateUserResponse();
        createUserResponse.setId(1L);
        createUserResponse.setUsername("testuser");
        createUserResponse.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(authorities = "USER_CREATE")
    public void givenComputedAuthority_whenCreateUser_thenReturnsCreated() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createUserResponse);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "USER_READ")
    public void givenComputedAuthority_whenGetUserById_thenReturnsUser() throws Exception {
        when(userService.findUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "USER_READ")
    public void givenComputedAuthority_whenGetAllUsers_thenReturnsPagedResponse() throws Exception {
        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>(new PageImpl<>(List.of(userResponse)));
        when(userService.findAllUsers(any(), any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "USER_UPDATE")
    public void givenComputedAuthority_whenUpdatePartialUser_thenReturnsUpdated() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1L);
        request.setFullName("Updated Name");

        UpdateUserResponse response = new UpdateUserResponse();
        response.setId(1L);
        response.setFullName("Updated Name");

        when(userService.updatePartialUser(any(UpdateUserRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Updated Name"));
    }

    @Test
    @WithMockUser(authorities = "USER_DELETE")
    public void givenComputedAuthority_whenDeleteUser_thenReturnsOk() throws Exception {
        // void return, nothing to mock validation-wise mainly
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "WRONG_AUTHORITY")
    public void givenWrongAuthority_whenCreateUser_thenReturnsForbidden() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
