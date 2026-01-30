package com.wms.backend.auth.dto.response;

import com.wms.backend.auth.entity.Permission;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Set<Permission> permissions;
    private Instant createdAt;
    private Instant updatedAt;
}
