package com.wms.backend.auth.mapper;

import com.wms.backend.auth.dto.request.CreateRoleRequest;
import com.wms.backend.auth.dto.request.UpdateRoleRequest;
import com.wms.backend.auth.dto.response.RoleResponse;
import com.wms.backend.auth.entity.Permission;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Named;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

    @Autowired
    protected PermissionRepository permissionRepository;

    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "mapPermissions")
    public abstract Role toEntity(CreateRoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "permissions", ignore = true)
    public abstract void updateEntityFromRequest(UpdateRoleRequest request, @MappingTarget Role role);

    public abstract RoleResponse toResponse(Role role);

    @Named("mapPermissions")
    protected Set<Permission> mapPermissions(List<String> permissions) {
        if (permissions == null) {
            return null;
        }
        return new HashSet<>(permissionRepository.findByCodeIn(permissions));
    }
}
