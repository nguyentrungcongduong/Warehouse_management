package com.wms.backend.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleRequest {
    @NotNull(message = "Id is required")
    private Long id;

    private String name;
    private String description;
    private Boolean active;
    private List<String> permissions;
}
