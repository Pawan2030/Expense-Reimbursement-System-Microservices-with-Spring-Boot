package com.ps.asde.expense.expense_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ps.asde.expense.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense.expense_service.dto.ReimbursementResponseDTO;
import com.ps.asde.expense.expense_service.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController controller;

    @Mock
    private HttpServletRequest request;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ReimbursementRequestDTO requestDTO;
    private ReimbursementResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        requestDTO = new ReimbursementRequestDTO();
        requestDTO.setEmployeeId(1L);
        requestDTO.setManagerId(2L);
        requestDTO.setAmount(new BigDecimal("1000.00"));
        requestDTO.setDescription("Travel expenses");

        responseDTO = new ReimbursementResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEmployeeId(1L);
        responseDTO.setManagerId(2L);
        responseDTO.setAmount(new BigDecimal("1000.00"));
        responseDTO.setDescription("Travel expenses");
        responseDTO.setStatus("PENDING");
    }

    @Test
    void testCreate_success() throws Exception {
        when(expenseService.create(any(ReimbursementRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/reimbursements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(expenseService, times(1)).create(any(ReimbursementRequestDTO.class));
    }

    @Test
    void testGetById_success() throws Exception {
        when(expenseService.getById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/reimbursements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()));
    }

    @Test
    void testListByEmployee_success() throws Exception {
        when(expenseService.listByEmployee(1L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/reimbursements")
                        .param("employeeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(1));
    }

    @Test
    void testListByManager_success() throws Exception {
        when(expenseService.listByManager(2L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/reimbursements")
                        .param("managerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].managerId").value(2));
    }

    @Test
    void testListAll_success() throws Exception {
        when(expenseService.listAll()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/reimbursements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDTO.getId()));
    }

    @Test
    void testApprove_success() throws Exception {
        responseDTO.setStatus("APPROVED");

        when(request.getAttribute("role")).thenReturn("MANAGER");
        when(request.getAttribute("managerId")).thenReturn(2L);
        when(expenseService.getById(1L)).thenReturn(responseDTO);
        when(expenseService.approve(1L, 2L)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/reimbursements/1/approve")
                        .requestAttr("role", "MANAGER")
                        .requestAttr("managerId", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void testApprove_forbidden_wrongRole() throws Exception {
        when(request.getAttribute("role")).thenReturn("EMPLOYEE");

        mockMvc.perform(put("/api/reimbursements/1/approve")
                        .requestAttr("role", "EMPLOYEE")
                        .requestAttr("managerId", 2L))
                .andExpect(status().isForbidden());
    }

    @Test
    void testReject_success() throws Exception {
        responseDTO.setStatus("REJECTED");

        when(request.getAttribute("role")).thenReturn("MANAGER");
        when(request.getAttribute("managerId")).thenReturn(2L);
        when(expenseService.getById(1L)).thenReturn(responseDTO);
        when(expenseService.reject(1L, 2L)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/reimbursements/1/reject")
                        .requestAttr("role", "MANAGER")
                        .requestAttr("managerId", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void testReject_forbidden_wrongRole() throws Exception {
        when(request.getAttribute("role")).thenReturn("EMPLOYEE");

        mockMvc.perform(put("/api/reimbursements/1/reject")
                        .requestAttr("role", "EMPLOYEE")
                        .requestAttr("managerId", 2L))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDelete_success() throws Exception {
        doNothing().when(expenseService).delete(1L, 1L);

        mockMvc.perform(delete("/api/reimbursements/1")
                        .param("actorId", "1"))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).delete(1L, 1L);
    }
}
