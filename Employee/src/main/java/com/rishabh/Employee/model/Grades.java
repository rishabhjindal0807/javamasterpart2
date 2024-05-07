package com.rishabh.Employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Grades {
    M1("M1",1),
    M2("M2",2),
    M3("M3",3),
    M4("M4",4),
    M5("M5",5),
    M6("M6",6),
    M7("M7",7),
    M8("M8",8),
    M9("M9",9);

    private final String grade;
    private final Integer gradeValue;
}
