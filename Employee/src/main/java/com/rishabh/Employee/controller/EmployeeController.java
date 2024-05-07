package com.rishabh.Employee.controller;

import com.rishabh.Employee.model.EmployeeDto;
import com.rishabh.Employee.model.GetEmployeeRequest;
import com.rishabh.Employee.service.Auth.AuthService;
import com.rishabh.Employee.service.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    AuthService authService;

    @GetMapping("/employee")
    public ResponseEntity<?> getAllEmployees(@RequestParam(required = false) String authToken,
                                             @RequestParam(required = false) String employeeId,
                                             @RequestParam(required = false) String firstName,
                                             @RequestParam(required = false) String lastName,
                                             @RequestParam(required = false) String dob,
                                             @RequestParam(required = false) String doj,
                                             @RequestParam(required = false) String grade) {
        if (!authService.isAuthenticated(authToken)) {
            return ResponseEntity.badRequest().body("Invalid authentication token");
        }
        boolean isEmpIdPresent = employeeId != null && !employeeId.isEmpty();
        boolean isFirstNamePresent = firstName != null && !firstName.isEmpty();
        boolean isLastNamePresent = lastName != null && !lastName.isEmpty();
        if (isEmpIdPresent || isFirstNamePresent || isLastNamePresent) {
            GetEmployeeRequest request = GetEmployeeRequest.builder()
                    .employeeId(employeeId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .dob(dob)
                    .doj(doj)
                    .grade(grade)
                    .build();
            List<EmployeeDto> response = employeeService.getAllEmployees(request);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Atleast one of the following must be present for search : Employee ID, First Name or Last Name");
        }
    }

    @GetMapping("/employee/{empid}")
    public ResponseEntity<?> getEmployeeById(@PathVariable String empid,
                                             @RequestParam(required = false) String authToken) {
        if (!authService.isAuthenticated(authToken)) {
            return ResponseEntity.badRequest().body("Invalid authentication token");
        }
        EmployeeDto employeeDto = employeeService.getEmployeeById(empid);
        return ResponseEntity.ok(employeeDto);
    }

    @PutMapping("/employee/{empid}")
    public ResponseEntity<?> modifyEmployee(@PathVariable String empid,
                                            @RequestBody EmployeeDto employeeDto,
                                            @RequestParam(required = false) String authToken) {
        if (!authService.isAuthenticated(authToken)) {
            return ResponseEntity.badRequest().body("Invalid authentication token");
        }

        EmployeeDto response = employeeService.modifyEmployee(empid, employeeDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/employee/import")
    public ResponseEntity<?> importEmployees(@RequestBody MultipartFile file,
                                             @RequestParam(required = false) String authToken) {

        try {
            if (!authService.isAuthenticated(authToken)) {
                return ResponseEntity.badRequest().body("Invalid authentication token");
            }
            List<EmployeeDto> response = employeeService.importEmployee(file);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/employee/export")
    public ResponseEntity<?> exportEmployee(@RequestParam(required = false) String authToken,
                                            @RequestParam(required = false) String employeeId,
                                            @RequestParam(required = false) String firstName,
                                            @RequestParam(required = false) String lastName,
                                            @RequestParam(required = false) String dob,
                                            @RequestParam(required = false) String doj,
                                            @RequestParam(required = false) String grade) {
        try {
            if (!authService.isAuthenticated(authToken)) {
                return ResponseEntity.badRequest().body("Invalid authentication token");
            }
            boolean isEmpIdPresent = employeeId != null && !employeeId.isEmpty();
            boolean isFirstNamePresent = firstName != null && !firstName.isEmpty();
            boolean isLastNamePresent = lastName != null && !lastName.isEmpty();
            if (isEmpIdPresent || isFirstNamePresent || isLastNamePresent) {
                GetEmployeeRequest request = GetEmployeeRequest.builder()
                        .employeeId(employeeId)
                        .firstName(firstName)
                        .lastName(lastName)
                        .dob(dob)
                        .doj(doj)
                        .grade(grade)
                        .build();
                List<EmployeeDto> employees = employeeService.getAllEmployees(request);
                Resource response = employeeService.exportEmployee(employees);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Atleast one of the following must be present for search : Employee ID, First Name or Last Name");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
}
