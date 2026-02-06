package com.wms.backend.auth.repository;

import com.wms.backend.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.wms.backend.config.DatabaseInitializer databaseInitializer;

    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .fullName("Test User")
                .active(true)
                .build();
    }

    @Test
    public void givenUser_whenSaved_thenCanBeFoundById() {
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void givenUser_whenSaved_thenCanBeFoundByUsername() {
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void givenUser_whenSaved_thenCanBeFoundByEmail() {
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findOneByEmail("test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void givenUser_whenCheckedByEmail_thenExistsReturnTrue() {
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("test@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    public void givenUser_whenCheckedByUsername_thenExistsReturnTrue() {
        userRepository.save(user);
        boolean exists = userRepository.existsByUsername("testuser");
        assertThat(exists).isTrue();
    }

    @Test
    public void givenUserWithRefreshToken_whenFindByTokenAndEmail_thenReturnsUser() {
        user.setRefreshToken("sample_refresh_token");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByRefreshTokenAndEmail("sample_refresh_token",
                "test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRefreshToken()).isEqualTo("sample_refresh_token");
    }

    @Test
    public void givenNoUser_whenFindByTokenAndEmail_thenReturnsEmpty() {
        Optional<User> foundUser = userRepository.findByRefreshTokenAndEmail("nonexistent_token", "test@example.com");
        assertThat(foundUser).isEmpty();
    }
}
