package com.wms.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private Boolean active;
    private List<String> permissions;
}
