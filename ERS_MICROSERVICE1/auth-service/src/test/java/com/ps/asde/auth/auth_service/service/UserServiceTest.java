package com.ps.asde.auth.auth_service.service;

import com.ps.asde.auth.auth_service.model.User;
import com.ps.asde.auth.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByUsername_userExists_returnsOptionalUser() {
        User user = new User();
        user.setUsername("alice");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("alice");

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("alice");
    }

    @Test
    void findByUsername_userNotFound_returnsEmptyOptional() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("bob");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("bob");
    }

    @Test
    void saveUser_passwordIsEncodedAndUserSaved() {
        User user = new User();
        user.setUsername("charlie");
        user.setPasswordHash("plainPassword");

        // simulate repository save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.saveUser(user);

        assertNotNull(saved);
        assertEquals("charlie", saved.getUsername());
        assertNotEquals("plainPassword", saved.getPasswordHash());
        assertTrue(userService.checkPassword("plainPassword", saved.getPasswordHash()));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void checkPassword_returnsTrueIfMatches() {
        String raw = "mypassword";
        String encoded = new BCryptPasswordEncoder().encode(raw);

        assertTrue(userService.checkPassword(raw, encoded));
        assertFalse(userService.checkPassword("wrong", encoded));
    }
}
