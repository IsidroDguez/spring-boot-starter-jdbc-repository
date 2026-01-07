package com.cmeza.spring.jdbc.repository.mappers.projections;

import com.cmeza.spring.jdbc.repository.projections.EmployeeAndTitleProjection;
import com.cmeza.spring.jdbc.repository.mappers.Attributes;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EmployeeAndTitleProjectionRowMapper extends JdbcProjectionRowMapper<EmployeeAndTitleProjection> {

    private final TitleProjectionRowMapper titleProjectionRowMapper;

    protected EmployeeAndTitleProjectionRowMapper(TitleProjectionRowMapper titleProjectionRowMapper) {
        super(EmployeeAndTitleProjection.class);
        this.titleProjectionRowMapper = titleProjectionRowMapper;
    }

    @Override
    protected void mapPropertyDescriptor(ResultSet rs, PropertyDescriptor propertyDescriptor, Attributes attributes, int rowNum) throws SQLException {
        attributes.addAttribute("titleObject", titleProjectionRowMapper.mapRow(rs, rowNum));
    }

    @Override
    public EmployeeAndTitleProjection mapRow(ResultSet rs, int rowNum) throws SQLException {
        return super.mapRow(rs, rowNum);
    }
}
