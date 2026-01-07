package com.cmeza.spring.jdbc.repository.mappers.records;

public class DefaultJdbcRecordRowMapper<T extends Record> extends JdbcRecordRowMapper<T> {
    protected DefaultJdbcRecordRowMapper(Class<T> mappedClass) {
        super(mappedClass);
    }
}
