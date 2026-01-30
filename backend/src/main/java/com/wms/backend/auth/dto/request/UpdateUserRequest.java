package com.wms.backend.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserRequest {
    @NotNull(message = "Id is required")
    private Long id;

    private String fullName;
    private String phone;
    private Long warehouseId;
    private Boolean active;
    private List<String> roles;
}
