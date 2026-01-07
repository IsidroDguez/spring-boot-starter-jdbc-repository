package com.cmeza.spring.jdbc.repository.mappers.classes;

public class DefaultJdbcRowMapper<T> extends JdbcRowMapper<T> {
    protected DefaultJdbcRowMapper(Class<T> mappedClass) {
        super(mappedClass);
    }
}
