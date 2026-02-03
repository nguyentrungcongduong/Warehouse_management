package com.wms.backend.auth.service;

import com.wms.backend.auth.dto.request.CreateUserRequest;
import com.wms.backend.auth.dto.response.CreateUserResponse;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.mapper.UserMapper;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.auth.service.impl.UserServiceImpl;
import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.shared.exception.EmailAlreadyUsedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    public void setup() {
        // Set value mainly for refresh token
        ReflectionTestUtils.setField(userService, "refreshTokenExpiration", 86400L);

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .active(true)
                .build();

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setPassword("password");
    }

    @Test
    public void givenExistingEmail_whenFindByEmail_thenReturnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByEmail("test@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(1L);
    }

    @Test
    public void givenNewUser_whenCreateUser_thenSavedAndReturned() {
        User newUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .active(true)
                .build();

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(newUser);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        CreateUserResponse response = new CreateUserResponse();
        response.setId(1L);
        response.setUsername("testuser");
        response.setEmail("test@example.com");

        when(userMapper.toCreateResponse(any(User.class))).thenReturn(response);

        CreateUserResponse savedResponse = userService.createUser(createUserRequest);

        assertThat(savedResponse).isNotNull();
        assertThat(savedResponse.getUsername()).isEqualTo("testuser");
        verify(auditLogService, times(1)).log(eq("CREATE"), eq("User"), eq(1L), eq(null), any(User.class));
    }

    @Test
    public void givenExistingUsername_whenCreateUser_thenThrowsException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void givenUserAndToken_whenUpdateUserToken_thenTokenUpdated() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserToken("test@example.com", "newToken");

        assertThat(user.getRefreshToken()).isEqualTo("newToken");
        assertThat(user.getRefreshTokenExpiredAt()).isNotNull();
    }

    @Test
    public void givenUserAndNullToken_whenUpdateUserToken_thenTokenCleared() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserToken("test@example.com", null);

        assertThat(user.getRefreshToken()).isNull();
        assertThat(user.getRefreshTokenExpiredAt()).isNull();
    }
}
