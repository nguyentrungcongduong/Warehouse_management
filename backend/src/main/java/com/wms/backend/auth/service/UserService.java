package com.wms.backend.auth.service;

import com.wms.backend.auth.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsById(Long id);

    User save(User user);

    void updateUserToken(String email, String token);

    Optional<User> findUserByRefreshTokenAndEmail(String token, String email);

    void delete(Long id);

    // New methods using DTOs
    com.wms.backend.auth.dto.response.CreateUserResponse createUser(
            com.wms.backend.auth.dto.request.CreateUserRequest request);

    com.wms.backend.auth.dto.response.UserResponse findUserById(Long id);

    com.wms.backend.shared.dto.response.PagedResponse<com.wms.backend.auth.dto.response.UserResponse> findAllUsers(
            org.springframework.data.jpa.domain.Specification<User> spec,
            org.springframework.data.domain.Pageable pageable);

    com.wms.backend.auth.dto.response.UpdateUserResponse updatePartialUser(
            com.wms.backend.auth.dto.request.UpdateUserRequest request);

    void deleteUser(Long id);
}
