package com.wms.backend.auth.mapper;

import com.wms.backend.auth.dto.request.CreateUserRequest;
import com.wms.backend.auth.dto.request.UpdateUserRequest;
import com.wms.backend.auth.dto.response.CreateUserResponse;
import com.wms.backend.auth.dto.response.UpdateUserResponse;
import com.wms.backend.auth.dto.response.UserResponse;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.RoleRepository;
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
public abstract class UserMapper {

    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    @Mapping(target = "password", ignore = true) // Handled in service
    public abstract User toEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true) // Update logic might be complex, or handle manually
    public abstract void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);

    public abstract UserResponse toResponse(User user);

    public abstract CreateUserResponse toCreateResponse(User user);

    public abstract UpdateUserResponse toUpdateResponse(User user);

    @Named("mapRoles")
    protected Set<Role> mapRoles(List<String> roles) {
        if (roles == null) {
            return null;
        }
        return new HashSet<>(roleRepository.findByNameIn(roles));
    }
}
