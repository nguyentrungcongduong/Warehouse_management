package com.wms.backend.auth.service.impl;

import com.wms.backend.auth.dto.request.CreateUserRequest;
import com.wms.backend.auth.dto.request.UpdateUserRequest;
import com.wms.backend.auth.dto.response.CreateUserResponse;
import com.wms.backend.auth.dto.response.UpdateUserResponse;
import com.wms.backend.auth.dto.response.UserResponse;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.mapper.UserMapper;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.auth.service.UserService;
import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.shared.exception.EntityNotFoundException;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.EmailAlreadyUsedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    @Value("${security.authentication.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final static String ENTITY_NAME = "User";

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        log.debug("Checking if user exists by username: {}", username);
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.debug("Checking if user exists by id: {}", id);
        return userRepository.existsById(id);
    }

    @Override
    public User save(User user) {
        log.debug("Saving user: {}", user);
        boolean isNew = user.getId() == null;
        User oldUser = null;
        if (!isNew) {
            oldUser = userRepository.findById(user.getId()).orElse(null);
        }

        User savedUser = userRepository.save(user);

        if (isNew) {
            auditLogService.log("CREATE", ENTITY_NAME, savedUser.getId(), null, savedUser);
        } else if (oldUser != null) {
            auditLogService.log("UPDATE", ENTITY_NAME, savedUser.getId(), oldUser, savedUser);
        }

        return savedUser;
    }

    @Override
    public void updateUserToken(String email, String token) {
        log.debug("update User token with email: {}, token: {}", email, token);
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));

        user.setRefreshToken(token);
        if (token != null) {
            user.setRefreshTokenExpiredAt(Instant.now().plusSeconds(refreshTokenExpiration));
            user.setLastLogin(Instant.now());
        } else {
            user.setRefreshTokenExpiredAt(null);
        }
        this.userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByRefreshTokenAndEmail(String token, String email) {
        log.debug("find User by refresh token and email: {}, {}", token, email);
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting user by id: {}", id);
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));

        this.userRepository.delete(user);
        auditLogService.log("DELETE", ENTITY_NAME, user.getId(), user, null);
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        log.info("Request to create user: {}", request.getUsername());
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EmailAlreadyUsedException();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException();
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        User savedUser = this.save(user);

        return userMapper.toCreateResponse(savedUser);
    }

    @Override
    public UserResponse findUserById(Long id) {
        log.info("Request to find user by id: {}", id);
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));

        return userMapper.toResponse(user);
    }

    @Override
    public PagedResponse<UserResponse> findAllUsers(Specification<User> spec, Pageable pageable) {
        log.info("Request to find all users");
        Page<User> page = this.userRepository.findAll(spec, pageable);

        Page<UserResponse> dtoPage = page.map(userMapper::toResponse);

        return new PagedResponse<>(dtoPage);
    }

    @Override
    public UpdateUserResponse updatePartialUser(UpdateUserRequest request) {
        log.info("Request to update partial user: {}", request.getId());
        User user = this.userRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));

        userMapper.updateEntityFromRequest(request, user);

        User savedUser = this.save(user);

        return userMapper.toUpdateResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Request to delete user by id: {}", id);
        this.delete(id);
    }
}
