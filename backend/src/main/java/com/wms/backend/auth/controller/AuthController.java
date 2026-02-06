package com.wms.backend.auth.controller;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wms.backend.auth.dto.request.ChangePasswordRequest;
import com.wms.backend.auth.dto.request.KeyAndPasswordRequest;
import com.wms.backend.auth.dto.request.LoginUserRequest;
import com.wms.backend.auth.dto.response.LoginUserResponse;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.service.UserService;
import com.wms.backend.shared.exception.EntityNotFoundException;
import com.wms.backend.shared.exception.InvalidPasswordException;
import com.wms.backend.shared.service.MailService;
import com.wms.backend.shared.util.SecurityUtil;
import com.wms.backend.shared.util.anotation.ApiMessage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Auth", description = "Authentication and user account management")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;

    private final UserService userService;

    private final MailService mailService;

    @Value("${security.authentication.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${security.authentication.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final int PASSWORD_MIN_LENGTH = 6;
    private final int PASSWORD_MAX_LENGTH = 30;

    @PostMapping("/auth/login")
    @ApiMessage("Login successful")
    @Operation(summary = "Login user", description = "Authenticate user and return access token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginUserRequest loginDTO) {
        log.info("REST request to login Auth: {}", loginDTO);

        // check user is exist
        Optional<User> user = userService.findByEmail(loginDTO.getUserName());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found", loginDTO.getUserName(), "usernotfound");
        }

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticateUser(loginDTO);

        // Lấy thông tin người dùng
        LoginUserResponse resLoginDTO = buildUserInfoDTO(loginDTO.getUserName());

        // Tạo Access Token
        String accessToken = securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccess_token(accessToken);

        // Tạo Refresh Token và cập nhật DB
        String refreshToken = securityUtil.createFreshToken(loginDTO.getUserName(), resLoginDTO);
        userService.updateUserToken(loginDTO.getUserName(), refreshToken);

        // Tạo HTTP-Only cookie
        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(refreshToken);

        // Gán Authentication vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về response
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get new access token")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    @ApiResponse(responseCode = "200", description = "Get new access token")
    public ResponseEntity<Object> handleRefreshToken(@CookieValue(name = "refresh_token") String refresh_token) {
        log.info("REST request to refresh Auth token, refresh_token: {}", refresh_token);
        // check refresh token
        Jwt decodedJwt = this.securityUtil.checkValidPrefreshToken(refresh_token);
        String email = decodedJwt.getSubject();

        // check user by email and token
        Optional<User> currentUser = this.userService.findUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser.isEmpty()) {
            throw new EntityNotFoundException("Refresh token is invalid or expired!", "Authentication",
                    "invalid_refresh_token");
        }

        if (currentUser.get().getRefreshTokenExpiredAt() == null) {
            throw new EntityNotFoundException("Invalid User", "login", "refresh_token_expired");
        }

        // 2. Lấy thông tin người dùng
        LoginUserResponse resLoginDTO = buildUserInfoDTO(currentUser.get().getEmail());

        // create token
        String access_token = this.securityUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccess_token(access_token);

        // Create refresh token and update DB
        String new_refresh_token = this.securityUtil.createFreshToken(email, resLoginDTO);
        this.userService.updateUserToken(email, new_refresh_token);

        // 5. Tạo HTTP-Only cookie
        ResponseCookie springCookie = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(resLoginDTO);
    }

    private Authentication authenticateUser(LoginUserRequest loginDTO) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUserName(),
                loginDTO.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authToken);
    }

    private LoginUserResponse buildUserInfoDTO(String email) {
        Optional<User> user = userService.findByEmail(email);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found", "User", "usernotfound");
        }

        LoginUserResponse res = new LoginUserResponse();
        LoginUserResponse.InformationUser info = new LoginUserResponse.InformationUser(
                user.get().getId(), user.get().getUsername(), user.get().getEmail(), user.get().getRoles());
        res.setUser(info);

        return res;
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get account information")
    @Operation(summary = "Get account info", description = "Get current logged in user account information")
    @ApiResponse(responseCode = "200", description = "Get account information")
    public ResponseEntity<LoginUserResponse.UserGetAccount> getAccount() {
        log.info("REST request to get Auth account");
        // get information of user from SecurityContext
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email == null) {
            throw new EntityNotFoundException("User not found", email, "usernotfound");
        }
        // Get user from DB
        Optional<User> currentUserDB = this.userService.findByEmail(email);
        if (currentUserDB.isEmpty()) {
            throw new EntityNotFoundException("User not found", email, "usernotfound");
        }
        LoginUserResponse.InformationUser userLogin = new LoginUserResponse.InformationUser(
                currentUserDB.get().getId(),
                currentUserDB.get().getUsername(),
                currentUserDB.get().getEmail(),
                currentUserDB.get().getRoles());

        LoginUserResponse.UserGetAccount userGetAccount = new LoginUserResponse.UserGetAccount(userLogin);

        return ResponseEntity.ok(userGetAccount);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout successful")
    @Operation(summary = "Logout user", description = "Logout user and invalidate refresh token")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public ResponseEntity<Void> handleLogout() {
        log.info("REST request to logout Auth");
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;

        // remove refresh token
        this.userService.updateUserToken(email, null);

        // remove cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();

    }

    @PostMapping("/auth/reset-password/init")
    @ApiMessage("Request password reset successful")
    @Operation(summary = "Request password reset", description = "Request password reset")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            // Pretend the request has been successful to prevent checking which emails
            // really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    @PostMapping("/auth/reset-password/finish")
    @ApiMessage("Reset passsword password successful")
    @Operation(summary = "Reset password", description = "Reset password")
    public void finishPasswordReset(@RequestBody KeyAndPasswordRequest keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(),
                keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new EntityNotFoundException("No user was found for this reset key", "User", "usernotfound");
        }
    }

    @PostMapping("/auth/change-password")
    @ApiMessage("Change password successful")
    @Operation(summary = "Change password", description = "Change password")
    @ApiResponse(responseCode = "200", description = "Change password successful")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        if (isPasswordLengthInvalid(request.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        this.userService.changePassword(request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    private boolean isPasswordLengthInvalid(String password) {
        return (StringUtils.isEmpty(password) ||
                password.length() < PASSWORD_MIN_LENGTH ||
                password.length() > PASSWORD_MAX_LENGTH);
    }

}
