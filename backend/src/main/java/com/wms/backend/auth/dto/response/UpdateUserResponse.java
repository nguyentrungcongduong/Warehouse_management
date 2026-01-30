package com.wms.backend.auth.dto.response;

import com.wms.backend.auth.entity.Role;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class UpdateUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Long warehouseId;
    private Boolean active;
    private Set<Role> roles;
    private Instant updatedAt;
}
