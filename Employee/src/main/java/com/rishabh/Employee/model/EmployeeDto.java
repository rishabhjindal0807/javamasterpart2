package com.rishabh.Employee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    private String employeeId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private LocalDate doj;
    private Grades grade;

}
