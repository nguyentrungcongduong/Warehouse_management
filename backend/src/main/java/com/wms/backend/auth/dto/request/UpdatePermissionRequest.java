package com.wms.backend.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePermissionRequest {
    @NotNull(message = "Id is required")
    private Long id;

    private String code;
    private String name;
    private String module;
    private String description;
}
