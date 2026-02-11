package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Employee {
    
    @Column(name="emp_id")
    @Id
    private Long empId;
    
    @Column(name="hire_date")
    private LocalDate hireDate;
    
    @Column(name="user_id")
    private Long userId;
    
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
}
