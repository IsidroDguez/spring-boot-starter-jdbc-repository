package com.cmeza.spring.jdbc.repository.records;

import java.util.Date;

public record TitleRecord(
        Integer employeeId,
        String title,
        Date fromDate,
        Date toDate) {
}
