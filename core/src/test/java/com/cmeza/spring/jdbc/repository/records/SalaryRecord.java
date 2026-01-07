package com.cmeza.spring.jdbc.repository.records;

import java.util.Date;

public record SalaryRecord(
        Integer employeeId,
        Double amount,
        Date fromDate,
        Date toDate) {
}
