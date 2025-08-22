package com.ps.asde.expense.expense_service.service;

import com.ps.asde.expense.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense.expense_service.dto.ReimbursementResponseDTO;
import com.ps.asde.expense.expense_service.exception.ResourceNotFoundException;
import com.ps.asde.expense.expense_service.model.Reimbursement;
import com.ps.asde.expense.expense_service.repository.ReimbursementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceImplTest {

    @Mock
    private ReimbursementRepository repository;

    @InjectMocks
    private ExpenseServiceImpl service;

    private Reimbursement reimbursement;
    private ReimbursementRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDTO = new ReimbursementRequestDTO();
        requestDTO.setEmployeeId(1L);
        requestDTO.setManagerId(2L);
        requestDTO.setAmount(new BigDecimal("1000.00"));
        requestDTO.setDescription("Travel expenses");

        reimbursement = new Reimbursement();
        reimbursement.setId(1L);
        reimbursement.setEmployeeId(1L);
        reimbursement.setManagerId(2L);
        reimbursement.setAmount(new BigDecimal("1000.00"));
        reimbursement.setDescription("Travel expenses");
        reimbursement.setStatus(Reimbursement.Status.PENDING);
        reimbursement.setActionAt(null);
    }

    @Test
    void testCreate_success() {
        when(repository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponseDTO dto = service.create(requestDTO);

        assertNotNull(dto);
        assertEquals("PENDING", dto.getStatus());
        assertEquals(requestDTO.getEmployeeId(), dto.getEmployeeId());
        verify(repository, times(1)).save(any(Reimbursement.class));
    }

    @Test
    void testCreate_invalidEmployeeManager_sameIds() {
        requestDTO.setManagerId(1L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(requestDTO));

        assertEquals("employeeId and managerId cannot be the same", ex.getMessage());
    }

    @Test
    void testGetById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        ReimbursementResponseDTO dto = service.getById(1L);

        assertEquals("PENDING", dto.getStatus());
        assertEquals(reimbursement.getEmployeeId(), dto.getEmployeeId());
    }

    @Test
    void testGetById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void testListByEmployee_success() {
        when(repository.findByEmployeeId(1L)).thenReturn(Arrays.asList(reimbursement));

        List<ReimbursementResponseDTO> list = service.listByEmployee(1L);

        assertEquals(1, list.size());
        assertEquals("PENDING", list.get(0).getStatus());
    }

    @Test
    void testListByManager_success() {
        when(repository.findByManagerId(2L)).thenReturn(Arrays.asList(reimbursement));

        List<ReimbursementResponseDTO> list = service.listByManager(2L);

        assertEquals(1, list.size());
        assertEquals("PENDING", list.get(0).getStatus());
    }

    @Test
    void testListAll_success() {
        when(repository.findAll()).thenReturn(Arrays.asList(reimbursement));

        List<ReimbursementResponseDTO> list = service.listAll();

        assertEquals(1, list.size());
        assertEquals("PENDING", list.get(0).getStatus());
    }

    @Test
    void testApprove_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));
        when(repository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponseDTO dto = service.approve(1L, 2L);

        assertEquals("APPROVED", dto.getStatus());
        assertNotNull(dto.getActionAt());
        verify(repository, times(1)).save(any(Reimbursement.class));
    }

    @Test
    void testApprove_notPending() {
        reimbursement.setStatus(Reimbursement.Status.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve(1L, 2L));
        assertEquals("Only PENDING reimbursements can be approved", ex.getMessage());
    }

    @Test
    void testApprove_wrongManager() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve(1L, 999L));
        assertEquals("Only the assigned manager can approve this reimbursement", ex.getMessage());
    }

    @Test
    void testReject_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));
        when(repository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponseDTO dto = service.reject(1L, 2L);

        assertEquals("REJECTED", dto.getStatus());
        assertNotNull(dto.getActionAt());
        verify(repository, times(1)).save(any(Reimbursement.class));
    }

    @Test
    void testReject_notPending() {
        reimbursement.setStatus(Reimbursement.Status.REJECTED);
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.reject(1L, 2L));
        assertEquals("Only PENDING reimbursements can be rejected", ex.getMessage());
    }

    @Test
    void testReject_wrongManager() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.reject(1L, 999L));
        assertEquals("Only the assigned manager can reject this reimbursement", ex.getMessage());
    }

    @Test
    void testDelete_byEmployee_pending() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        service.delete(1L, 1L);

        verify(repository, times(1)).delete(reimbursement);
    }

    @Test
    void testDelete_byEmployee_notPending() {
        reimbursement.setStatus(Reimbursement.Status.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.delete(1L, 1L));
        assertEquals("Employee can delete only PENDING reimbursements", ex.getMessage());
    }

    @Test
    void testDelete_byManager_notAllowed() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.delete(1L, 2L));
        assertEquals("Manager deletion is not allowed. Use reject/approve actions.", ex.getMessage());
    }

    @Test
    void testDelete_byOther_notAllowed() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.delete(1L, 999L));
        assertEquals("Only the owner employee or assigned manager (for actions) can perform this operation", ex.getMessage());
    }
}
