package com.wms.backend.auth.repository;

import com.wms.backend.auth.entity.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.wms.backend.config.DatabaseInitializer databaseInitializer;

    private Permission permission;

    @BeforeEach
    public void setup() {
        permission = Permission.builder()
                .code("USER_READ")
                .name("Read User")
                .module("User")
                .build();
    }

    @Test
    public void givenPermission_whenSaved_thenCanBeFoundByCode() {
        permissionRepository.save(permission);
        Optional<Permission> found = permissionRepository.findByCode("USER_READ");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Read User");
    }

    @Test
    public void givenPermission_whenCheckedByCode_thenExistsReturnTrue() {
        permissionRepository.save(permission);
        boolean exists = permissionRepository.existsByCode("USER_READ");
        assertThat(exists).isTrue();
    }

    @Test
    public void givenPermission_whenCheckedByCodeAndNotId_thenReturnsTrueIfOtherId() {
        Permission saved = permissionRepository.save(permission);
        boolean exists = permissionRepository.existsByCodeAndIdNot("USER_READ", saved.getId() + 1);
        assertThat(exists).isTrue();
    }

    @Test
    public void givenPermission_whenCheckedByCodeAndSameId_thenReturnsFalse() {
        Permission saved = permissionRepository.save(permission);
        boolean exists = permissionRepository.existsByCodeAndIdNot("USER_READ", saved.getId());
        assertThat(exists).isFalse();
    }

    @Test
    public void givenDuplicatePermissionCode_whenSaved_thenThrowsException() {
        permissionRepository.save(permission);

        Permission duplicate = Permission.builder()
                .code("USER_READ")
                .name("Another Name")
                .module("Another Module")
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> {
                    permissionRepository.saveAndFlush(duplicate);
                });
    }
}
