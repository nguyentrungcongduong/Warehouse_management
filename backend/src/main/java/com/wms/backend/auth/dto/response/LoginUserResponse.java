package com.wms.backend.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import com.wms.backend.auth.entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponse {

    private String access_token;
    private InformationUser user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class InformationUser {
        private Long id;
        private String name;
        private String email;
        private Set<Role> roles;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserGetAccount {
        private InformationUser user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserInsideToken {
        private Long id;
        private String name;
        private String email;
    }
}
