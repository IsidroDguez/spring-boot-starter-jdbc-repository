package com.cmeza.spring.jdbc.repository.mappers.projections;

public class DefaultJdbcProjectionRowMapper<T> extends JdbcProjectionRowMapper<T> {
    protected DefaultJdbcProjectionRowMapper(Class<T> mappedClass) {
        super(mappedClass);
    }
}
