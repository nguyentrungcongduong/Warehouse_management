package com.wms.backend.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Email invalid")
    @NotBlank(message = "Email is required")
    private String email;

    private String fullName;
    private String phone;
    private Long warehouseId;
    private List<String> roles;
}
