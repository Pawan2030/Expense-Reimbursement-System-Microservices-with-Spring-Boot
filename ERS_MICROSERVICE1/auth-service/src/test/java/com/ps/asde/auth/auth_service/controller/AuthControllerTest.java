package com.ps.asde.auth.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ps.asde.auth.auth_service.dto.LoginRequest;
import com.ps.asde.auth.auth_service.dto.LoginResponse;
import com.ps.asde.auth.auth_service.dto.RegisterRequest;
import com.ps.asde.auth.auth_service.model.User;
import com.ps.asde.auth.auth_service.repository.UserRepository;
import com.ps.asde.auth.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole("EMPLOYEE");
        user.setEmployeeId(101L);
        user.setManagerId(201L);
    }

    @Test
    void testLogin_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString(), anyLong(), anyLong()))
                .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void testLogin_invalidUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("wrong");
        request.setPassword("password123");

        when(userRepository.findByUsername("wrong")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLogin_invalidPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("wrongpass");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testRegister_success_employee() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setPassword("pass123");
        request.setRole("EMPLOYEE");
        request.setEmployeeId(102L);
        request.setManagerId(201L);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void testRegister_success_manager() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("bob");
        request.setPassword("pass123");
        request.setRole("MANAGER");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void testRegister_usernameExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("pass123");
        request.setRole("EMPLOYEE");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    void testRegister_employeeMissingManagerId() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("eve");
        request.setPassword("pass123");
        request.setRole("EMPLOYEE");

        when(userRepository.findByUsername("eve")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Employee must be assigned to a manager (managerId required)"));
    }
}
