package com.ps.asde.employee.employee_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO {
    private Long id;          // Employee's primary DB ID
    private String username;
    private String role;
    private Long managerId;   // Manager's ID
    private Long employeeId;  // Employee's business ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
