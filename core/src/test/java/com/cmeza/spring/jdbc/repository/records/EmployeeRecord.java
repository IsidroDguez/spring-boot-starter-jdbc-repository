package com.cmeza.spring.jdbc.repository.records;

import java.util.Date;

public record EmployeeRecord(
        Integer id,
        Date birthDate,
        String firstName,
        String lastName,
        String gender,
        Date hireDate,
        SalaryRecord salary,
        TitleRecord title) {
    public EmployeeRecord(String lastName, String gender, Date hireDate, SalaryRecord salary, Integer id, Date birthDate, String firstName){
        this(id,birthDate, firstName,lastName,gender,hireDate,salary,null);
    }

    public EmployeeRecord(Date birthDate, String firstName, String lastName, String gender, Date hireDate, Integer id){
        this(id,birthDate, firstName,lastName,gender,hireDate,null,null);
    }
}
