package com.ps.asde.auth.auth_service.service;

import com.ps.asde.auth.auth_service.model.User;
import com.ps.asde.auth.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        // Arrange
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash("hashedPassword");
        user.setRole("EMPLOYEE");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("john");

        // Assert
        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE")));

        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown")
        );

        assertEquals("User not found: unknown", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
