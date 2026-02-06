package com.wms.backend.auth.service;

import com.wms.backend.auth.dto.request.CreateUserRequest;
import com.wms.backend.auth.dto.request.UpdateUserRequest;
import com.wms.backend.auth.dto.response.CreateUserResponse;
import com.wms.backend.auth.dto.response.UpdateUserResponse;
import com.wms.backend.auth.dto.response.UserResponse;
import com.wms.backend.auth.entity.User;
import com.wms.backend.shared.dto.response.PagedResponse;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
        Optional<User> findByUsername(String username);

        Optional<User> findByEmail(String email);

        boolean existsByUsername(String username);

        boolean existsById(Long id);

        User save(User user);

        void updateUserToken(String email, String token);

        Optional<User> findUserByRefreshTokenAndEmail(String token, String email);

        void delete(Long id);

        CreateUserResponse createUser(CreateUserRequest request);

        UserResponse findUserById(Long id);

        PagedResponse<UserResponse> findAllUsers(Specification<User> spec, Pageable pageable);

        UpdateUserResponse updatePartialUser(UpdateUserRequest request);

        void deleteUser(Long id);

        void changePassword(String currentPassword, String newPassword);

        Optional<User> requestPasswordReset(String mail);

        Optional<User> completePasswordReset(String newPassword, String key);
}
