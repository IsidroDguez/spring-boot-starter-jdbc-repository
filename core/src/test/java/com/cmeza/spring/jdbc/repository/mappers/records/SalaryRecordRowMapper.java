package com.cmeza.spring.jdbc.repository.mappers.records;

import com.cmeza.spring.jdbc.repository.records.SalaryRecord;
import org.springframework.stereotype.Component;

@Component
public class SalaryRecordRowMapper extends JdbcRecordRowMapper<SalaryRecord> {
    protected SalaryRecordRowMapper() {
        super(SalaryRecord.class);
    }
}
