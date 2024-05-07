package com.rishabh.Employee.service.employee.impl;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.rishabh.Employee.model.EmployeeDto;
import com.rishabh.Employee.model.GetEmployeeRequest;
import com.rishabh.Employee.model.Grades;
import com.rishabh.Employee.service.File.FileProcessingService;
import com.rishabh.Employee.service.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final Gson gson = new Gson();
    @Autowired
    FileProcessingService fileProcessingService;

    private HashMap<String, EmployeeDto> employeesMap;

    public EmployeeServiceImpl() {
        employeesMap = new HashMap<>();
    }

    @Override
    public List<EmployeeDto> getAllEmployees(GetEmployeeRequest getEmployeesRequest) {
        List<EmployeeDto> employees = new ArrayList<>();
        if (getEmployeesRequest.getEmployeeId() != null && !getEmployeesRequest.getEmployeeId().isEmpty()) {
            EmployeeDto employeeDto = employeesMap.get(getEmployeesRequest.getEmployeeId());
            if (employeeDto == null) {
                throw new RuntimeException("Employee not found with ID : " + getEmployeesRequest.getEmployeeId());
            }
            employees.add(employeeDto);
            return employees;
        }

        if (getEmployeesRequest.getFirstName() != null && !getEmployeesRequest.getFirstName().isEmpty()) {
            //Find all the employes and add it in the list of employees where getEmployeesRequest.getFirstName() can be equal to or can accomodate * for wildcard queries
            employees.addAll(employeesMap.values().stream()
                    .filter(employee -> employee.getFirstName().replace("*", "").startsWith(getEmployeesRequest.getFirstName()))
                    .collect(Collectors.toList()));

        }
        if (getEmployeesRequest.getLastName() != null && !getEmployeesRequest.getLastName().isEmpty()) {
            //Find all the employes and add it in the list of employees where getEmployeesRequest.getLastName() can be equal to or can accomodate * for wildcard queries
            employees.addAll(employeesMap.values().stream()
                    .filter(employee -> employee.getLastName().replace("*", "").startsWith(getEmployeesRequest.getLastName()))
                    .collect(Collectors.toList()));

        }
        // Filter by DOB (if provided)
        if (getEmployeesRequest.getDob() != null && !getEmployeesRequest.getDob().isEmpty()) {
            LocalDate dobFilterDate = getDate(keepAlphanumericAndHyphens(getEmployeesRequest.getDob()));
            employees = employees.stream()
                    .filter(employee -> {
                        if (getEmployeesRequest.getDob().contains("<"))
                            return employee.getDob().compareTo(dobFilterDate) < 0;
                        else if (getEmployeesRequest.getDob().contains(">"))
                            return employee.getDob().compareTo(dobFilterDate) > 0;
                        else return employee.getDob().equals(dobFilterDate);
                    })
                    .collect(Collectors.toList());
        }

        // Filter by DOJ (if provided)
        if (getEmployeesRequest.getDoj() != null && !getEmployeesRequest.getDoj().isEmpty()) {
            LocalDate dojFilterDate = getDate(keepAlphanumericAndHyphens(getEmployeesRequest.getDoj()));
            employees = employees.stream()
                    .filter(employee -> {
                        if (getEmployeesRequest.getDob().contains("<"))
                            return employee.getDoj().compareTo(dojFilterDate) < 0;
                        else if (getEmployeesRequest.getDob().contains(">"))
                            return employee.getDoj().compareTo(dojFilterDate) > 0;
                        else return employee.getDoj().equals(dojFilterDate);
                    })
                    .collect(Collectors.toList());
        }
        if (getEmployeesRequest.getGrade() != null && !getEmployeesRequest.getGrade().isEmpty()) {
            //Find all the employes and add it in the list of employees where getEmployeesRequest.getGrade() which has a prefix of =, <, or >
            Grades filterGrade = Grades.valueOf(keepAlphanumericAndHyphens(getEmployeesRequest.getGrade()));
            employees = employees.stream()
                    .filter(employee -> {
                        if (getEmployeesRequest.getDob().contains("<"))
                            return employee.getGrade().getGradeValue() < filterGrade.getGradeValue();
                        else if (getEmployeesRequest.getDob().contains(">"))
                            return employee.getGrade().getGradeValue() > filterGrade.getGradeValue();
                        else return employee.getGrade().getGradeValue().equals(filterGrade.getGradeValue());
                    })
                    .collect(Collectors.toList());

        }
        return employees;
    }

    @Override
    public EmployeeDto getEmployeeById(Object getEmployeesRequest) {
        return null;
    }

    @Override
    public EmployeeDto modifyEmployee(String empId, EmployeeDto employeeDto) {
        return null;
    }

    @Override
    public List<EmployeeDto> importEmployee(MultipartFile file) {
        JsonArray jsonElements = fileProcessingService.importFile(file);
        List<EmployeeDto> employeeDtoList = new ArrayList<>();
        jsonElements.forEach(jsonElement -> {
            System.out.println(jsonElement.toString());
            String empId = jsonElement.getAsJsonObject().get("empid").getAsString();
            String firstName = jsonElement.getAsJsonObject().get("firstName").getAsString();
            String lastName = jsonElement.getAsJsonObject().get("lastName").getAsString();

            String dobStr = jsonElement.getAsJsonObject().get("dob").getAsString();
            LocalDate dob = getDate(dobStr);
            String dojStr = jsonElement.getAsJsonObject().get("doj").getAsString();
            LocalDate doj = getDate(dojStr);

            String gradeStr = jsonElement.getAsJsonObject().get("grade").getAsString();
            Grades grade = Grades.valueOf(gradeStr);
            EmployeeDto employeeDto = EmployeeDto.builder()
                    .employeeId(empId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .dob(dob)
                    .doj(doj)
                    .grade(grade)
                    .build();
            employeeDtoList.add(employeeDto);
            employeesMap.put(empId, employeeDto);
        });
        return employeeDtoList;
    }

    private LocalDate getDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            return date;
        } catch (Exception e) {
            System.out.println("Invalid date format");
        }
        throw new RuntimeException("INVALID DATE FORMAT");
    }

    @Override
    public Resource exportEmployee(List<EmployeeDto> employeeDtoList) {
        List<GetEmployeeRequest> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        employeeDtoList.forEach(employeeDto -> {
            list.add(GetEmployeeRequest.builder()
                    .employeeId(employeeDto.getEmployeeId())
                    .grade(employeeDto.getGrade().getGrade())
                    .dob(employeeDto.getDob().format(formatter))
                    .doj(employeeDto.getDoj().format(formatter))
                    .firstName(employeeDto.getFirstName())
                    .lastName(employeeDto.getLastName())
                    .build());
        });
        String csvString = fileProcessingService.exportFile(gson.toJson(list));
        return createCsvResource(csvString, "sample.csv");

    }

    private String keepAlphanumericAndHyphens(String input) {
        // Regex pattern to match alphanumeric characters and hyphens
        String regex = "[^a-zA-Z0-9-]";
        // Replace all non-alphanumeric characters and hyphens with an empty string
        return input.replaceAll(regex, "");
    }

    public static Resource createCsvResource(String csvString, String filename) {
        // Convert CSV string to bytes
        byte[] bytes = csvString.getBytes(StandardCharsets.UTF_8);

        // Create ByteArrayInputStream from bytes
        InputStream inputStream = new ByteArrayInputStream(bytes);

        // Create ByteArrayResource from ByteArrayInputStream
        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename; // Set the filename for the resource
            }
        };
    }

}
