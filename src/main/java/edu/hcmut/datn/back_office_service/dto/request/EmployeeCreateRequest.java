package edu.hcmut.datn.back_office_service.dto.request;

import java.time.LocalDate;

import edu.hcmut.datn.back_office_service.dao.Employee;

public class EmployeeCreateRequest {

    private LocalDate hireDate;
    private Long userId;

    public Employee toEntity() {
        return new Employee(hireDate, userId);
    }
}
