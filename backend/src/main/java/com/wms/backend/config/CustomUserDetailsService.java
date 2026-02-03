package com.wms.backend.config;

import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.wms.backend.auth.service.UserService;
import com.wms.backend.shared.exception.EntityNotFoundException;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<com.wms.backend.auth.entity.User> user = this.userService.findByEmail(email);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("Invalid User", "login", "email_not_found");
        }

        return new User(
                user.get().getEmail(),
                user.get().getPassword(),
                user.get().getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList());
    }

}
