package com.wms.backend.auth.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import com.wms.backend.auth.dto.request.CreateUserRequest;
import com.wms.backend.auth.dto.request.UpdateUserRequest;
import com.wms.backend.auth.dto.response.CreateUserResponse;
import com.wms.backend.auth.dto.response.UserResponse;
import com.wms.backend.auth.dto.response.UpdateUserResponse;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.service.UserService;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.wms.backend.shared.util.anotation.ApiMessage;
import com.wms.backend.shared.exception.BadRequestAlertException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "User", description = "User management")
public class UserController {

    private static final String ENTITY_NAME = "user";

    private final UserService userService;

    @PostMapping("/users")
    @ApiMessage("User created successfully")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to create User: {}", request);
        CreateUserResponse createdUser = this.userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user information successfully")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Get user by id", description = "Retrieve user details by its ID")
    @ApiResponse(responseCode = "200", description = "Get user information successfully")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
        log.info("REST request to get User by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        return ResponseEntity.ok(this.userService.findUserById(id));
    }

    @GetMapping("/users")
    @ApiMessage("Get all users successfully")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination and filtering")
    @ApiResponse(responseCode = "200", description = "Get all users successfully")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(Pageable pageable,
            @Filter Specification<User> spec) {
        log.info("REST request to get all Users, pageable: {}", pageable);
        return ResponseEntity.ok(userService.findAllUsers(spec, pageable));
    }

    @PatchMapping("/users/{id}")
    @ApiMessage("User updated successfully")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Operation(summary = "Update user", description = "Update an existing user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    public ResponseEntity<UpdateUserResponse> updatePartialUser(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserRequest dto) {
        log.info("REST request to update User partially, id: {}, body: {}", id, dto);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idnull");
        }
        UpdateUserResponse updatedUser = this.userService.updatePartialUser(dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("User deleted successfully")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Operation(summary = "Delete user", description = "Delete a user by its ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        log.info("REST request to delete User by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
