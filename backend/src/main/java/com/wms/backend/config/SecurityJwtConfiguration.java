package com.wms.backend.config;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.repository.RoleRepository;
import com.wms.backend.shared.util.SecurityUtil;

@Configuration
public class SecurityJwtConfiguration {
    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtKey;

    private RoleRepository roleRepository;

    public SecurityJwtConfiguration(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> permissions = jwt.getClaimAsStringList("permissions");
            List<String> roles = jwt.getClaimAsStringList("roles");

            List<GrantedAuthority> authorities = new ArrayList<>();

            if (permissions != null) {
                permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }

            if (roles != null) {
                roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
                List<Role> roleDb = this.roleRepository.findByNameIn(roles);
                permissions = roleDb.stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission -> permission.getCode())
                        .toList();
                permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }

            return authorities;
        });

        return converter;
    }
}
