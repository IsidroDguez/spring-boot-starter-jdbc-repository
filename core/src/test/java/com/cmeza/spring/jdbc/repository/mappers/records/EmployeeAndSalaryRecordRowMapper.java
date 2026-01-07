package com.cmeza.spring.jdbc.repository.mappers.records;

import com.cmeza.spring.jdbc.repository.mappers.Attributes;
import com.cmeza.spring.jdbc.repository.records.EmployeeRecord;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EmployeeAndSalaryRecordRowMapper extends JdbcRecordRowMapper<EmployeeRecord> {

    private final SalaryRecordRowMapper salaryRecordRowMapper;

    protected EmployeeAndSalaryRecordRowMapper(SalaryRecordRowMapper salaryRecordRowMapper) {
        super(EmployeeRecord.class);
        this.salaryRecordRowMapper = salaryRecordRowMapper;
    }

    @Override
    protected void mapRecord(ResultSet rs, Attributes attributes, int rowNumber) throws SQLException {
        attributes.addAttribute("salary", salaryRecordRowMapper.mapRow(rs, rowNumber));
    }

    @Override
    public EmployeeRecord mapRow(ResultSet rs, int rowNumber) throws SQLException {
        return super.mapRow(rs, rowNumber);
    }
}
