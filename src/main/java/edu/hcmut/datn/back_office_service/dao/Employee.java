package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
public class Employee {

    @Column(name="emp_id")
    @Id
    @Getter
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long empId;

    @Column(name="hire_date")
    @Getter
    private LocalDate hireDate;

    @Column(name="user_id")
    @Getter
    private Long userId;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    protected Employee() {}

    // Contructor for creating purpose
    public Employee(LocalDate hireDate, Long userId) {
        this.hireDate = hireDate;
        this.userId = userId;
    }
}
