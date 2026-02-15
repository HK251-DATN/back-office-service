package edu.hcmut.datn.back_office_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.hcmut.datn.back_office_service.dao.Employee;
import edu.hcmut.datn.back_office_service.dto.request.EmployeeCreateRequest;
import edu.hcmut.datn.back_office_service.dto.response.ApiResponse;
import edu.hcmut.datn.back_office_service.service.EmployeeService;

@Controller
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> create(@RequestBody EmployeeCreateRequest employee) {
        try {
            Employee newEmp = employeeService.create(employee.toEntity());

            return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Create employee successfully", newEmp));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<Employee>> read(@PathVariable Long employeeId) {
        try {
            Employee emp = employeeService.read(employeeId);

            return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Read employee successfully", emp));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> readAll(Integer pageNum, Integer pageSize) {
        List<Employee> list = employeeService.readAll(pageNum, pageSize);

        if (list.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No Employee Exists", null));
        }

        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get all employees successfully", list));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long employeeId) {
        try {
            employeeService.delete(employeeId);

            return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Delete employee successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }
}
