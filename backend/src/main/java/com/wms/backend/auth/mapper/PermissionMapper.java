package com.wms.backend.auth.mapper;

import com.wms.backend.auth.dto.request.CreatePermissionRequest;
import com.wms.backend.auth.dto.request.UpdatePermissionRequest;
import com.wms.backend.auth.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toEntity(CreatePermissionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdatePermissionRequest request, @MappingTarget Permission permission);
}
