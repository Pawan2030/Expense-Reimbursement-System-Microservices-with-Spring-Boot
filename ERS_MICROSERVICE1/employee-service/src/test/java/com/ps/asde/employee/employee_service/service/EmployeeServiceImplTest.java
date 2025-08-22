package com.ps.asde.employee.employee_service.service;

import com.ps.asde.employee.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee.employee_service.dto.EmployeeResponseDTO;
import com.ps.asde.employee.employee_service.exception.ResourceNotFoundException;
import com.ps.asde.employee.employee_service.mapper.EmployeeMapper;
import com.ps.asde.employee.employee_service.model.Employee;
import com.ps.asde.employee.employee_service.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeServiceImpl service;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setEmployeeId(1L);
        requestDTO.setManagerId(2L);
        requestDTO.setUsername("John Doe");
        requestDTO.setPassword("test123");

        employee = EmployeeMapper.toEntity(requestDTO);
        employee.setId(1L);
    }

    @Test
    void testCreate() {
        when(repository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO response = service.create(requestDTO);

        assertNotNull(response);
        assertEquals(employee.getId(), response.getId());
        assertEquals(employee.getUsername(), response.getUsername());
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void testGetById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO response = service.getById(1L);

        assertNotNull(response);
        assertEquals(employee.getId(), response.getId());
    }

    @Test
    void testGetById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void testListByEmployee() {
        when(repository.findByEmployeeId(1L)).thenReturn(Collections.singletonList(employee));

        List<EmployeeResponseDTO> list = service.listByEmployee(1L);

        assertEquals(1, list.size());
        assertEquals(employee.getId(), list.get(0).getId());
    }

    @Test
    void testListByManager() {
        when(repository.findByManagerId(2L)).thenReturn(Collections.singletonList(employee));

        List<EmployeeResponseDTO> list = service.listByManager(2L);

        assertEquals(1, list.size());
        assertEquals(employee.getManagerId(), list.get(0).getManagerId());
    }

    @Test
    void testListAll() {
        when(repository.findAll()).thenReturn(Collections.singletonList(employee));

        List<EmployeeResponseDTO> list = service.listAll();

        assertEquals(1, list.size());
        assertEquals(employee.getId(), list.get(0).getId());
    }

    @Test
    void testDelete_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        service.delete(1L);

        verify(repository, times(1)).delete(employee);
    }

    @Test
    void testDelete_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).delete(any(Employee.class));
    }
}
