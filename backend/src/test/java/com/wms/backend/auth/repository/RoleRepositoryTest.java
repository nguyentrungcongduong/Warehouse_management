package com.wms.backend.auth.repository;

import com.wms.backend.auth.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.wms.backend.config.DatabaseInitializer databaseInitializer;

    private Role role;

    @BeforeEach
    public void setup() {
        role = Role.builder()
                .name("ROLE_USER")
                .description("Default user role")
                .active(true)
                .build();
    }

    @Test
    public void givenRole_whenSaved_thenCanBeFoundByName() {
        roleRepository.save(role);
        Optional<Role> foundRole = roleRepository.findByName("ROLE_USER");
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getDescription()).isEqualTo("Default user role");
    }

    @Test
    public void givenRole_whenCheckedByName_thenExistsReturnTrue() {
        roleRepository.save(role);
        boolean exists = roleRepository.existsByName("ROLE_USER");
        assertThat(exists).isTrue();
    }

    @Test
    public void givenRole_whenCheckedByNameAndNotId_thenReturnsTrueIfOtherId() {
        Role savedRole = roleRepository.save(role);
        boolean exists = roleRepository.existsByNameAndIdNot("ROLE_USER", savedRole.getId() + 1);
        assertThat(exists).isTrue();
    }

    @Test
    public void givenRole_whenCheckedByNameAndSameId_thenReturnsFalse() {
        Role savedRole = roleRepository.save(role);
        boolean exists = roleRepository.existsByNameAndIdNot("ROLE_USER", savedRole.getId());
        assertThat(exists).isFalse();
    }
}
