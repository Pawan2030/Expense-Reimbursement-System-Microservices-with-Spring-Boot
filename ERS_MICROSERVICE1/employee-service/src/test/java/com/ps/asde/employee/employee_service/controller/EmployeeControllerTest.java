package com.ps.asde.employee.employee_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ps.asde.employee.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee.employee_service.dto.EmployeeResponseDTO;
import com.ps.asde.employee.employee_service.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController controller;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private EmployeeRequestDTO employeeRequestDTO;
    private EmployeeResponseDTO employeeResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setUsername("John Doe");
        employeeRequestDTO.setEmployeeId(1L);
        employeeRequestDTO.setManagerId(2L);
        employeeRequestDTO.setPassword("password123");

        employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(1L);
        employeeResponseDTO.setUsername("John Doe");
        employeeResponseDTO.setEmployeeId(1L);
        employeeResponseDTO.setManagerId(2L);
    }

    @Test
    void testCreateEmployee_success() throws Exception {
        when(request.getAttribute("role")).thenReturn("MANAGER");
        when(request.getAttribute("employeeId")).thenReturn(2L);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(employeeService.create(any(EmployeeRequestDTO.class))).thenReturn(employeeResponseDTO);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDTO))
                        .requestAttr("role", "MANAGER")
                        .requestAttr("employeeId", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeResponseDTO.getId()))
                .andExpect(jsonPath("$.username").value(employeeResponseDTO.getUsername()));

        verify(employeeService, times(1)).create(any(EmployeeRequestDTO.class));
    }

    @Test
    void testGetEmployee_success() throws Exception {
        when(employeeService.getById(1L)).thenReturn(employeeResponseDTO);

        mockMvc.perform(get("/api/employees/1")
                        .requestAttr("role", "MANAGER")
                        .requestAttr("employeeId", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeResponseDTO.getId()))
                .andExpect(jsonPath("$.username").value(employeeResponseDTO.getUsername()));
    }

    @Test
    void testGetEmployee_forbidden() throws Exception {
        mockMvc.perform(get("/api/employees/1")
                        .requestAttr("role", "EMPLOYEE")
                        .requestAttr("employeeId", 2L))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListEmployees_byManager_success() throws Exception {
        when(employeeService.listByManager(2L)).thenReturn(Collections.singletonList(employeeResponseDTO));

        mockMvc.perform(get("/api/employees")
                        .param("managerId", "2")
                        .requestAttr("role", "MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(employeeResponseDTO.getId()));
    }

    @Test
    void testListEmployees_forbidden() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .requestAttr("role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteEmployee_success() throws Exception {
        mockMvc.perform(delete("/api/employees/1")
                        .requestAttr("role", "MANAGER"))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).delete(1L);
    }

    @Test
    void testDeleteEmployee_forbidden() throws Exception {
        mockMvc.perform(delete("/api/employees/1")
                        .requestAttr("role", "EMPLOYEE"))
                .andExpect(status().isForbidden());

        verify(employeeService, never()).delete(1L);
    }
}
