package com.ps.asde.auth.auth_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role;
    private Long employeeId;
    private Long managerId;// FIXED: was String, should be Long
}
