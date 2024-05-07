package com.rishabh.Employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GetEmployeeRequest{
    private String employeeId;
    private String firstName;
    private String lastName;
    private String dob;
    private String doj;
    private String grade;
}
