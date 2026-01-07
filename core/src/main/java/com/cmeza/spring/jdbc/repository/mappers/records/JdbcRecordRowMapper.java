package com.cmeza.spring.jdbc.repository.mappers.records;

import com.cmeza.spring.jdbc.repository.mappers.Attributes;
import com.cmeza.spring.jdbc.repository.mappers.records.utils.CustomDataClassRowMapper;
import com.cmeza.spring.jdbc.repository.mappers.records.utils.Records;
import lombok.Getter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public abstract class JdbcRecordRowMapper<T extends Record> implements RowMapper<T> {

    private final Class<T> mappedClass;
    private final CustomDataClassRowMapper<T> dataClassRowMapper;
    private final Attributes attributes;

    protected JdbcRecordRowMapper(Class<T> mappedClass) {
        Assert.notNull(mappedClass, "mappedClass must not be null");
        Assert.isTrue(mappedClass.isRecord(), "The mapped class must be an record");
        this.mappedClass = mappedClass;
        this.dataClassRowMapper = CustomDataClassRowMapper.newInstance(this.mappedClass);
        this.attributes = new RecordsAttributesImpl();
    }

    public static <T extends Record> JdbcRecordRowMapper<T> newInstance(Class<T> mappedClass) {
        return new DefaultJdbcRecordRowMapper<>(mappedClass);
    }

    protected void mapRecord(ResultSet rs, Attributes attributes, int rowNumber) throws SQLException {
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        T entity = dataClassRowMapper.mapRow(rs, rowNumber);
        Assert.notNull(entity, "The result should not be null");
        mapRecord(rs, attributes, rowNumber);

        Map<String, Object> attributesMap = attributes.getAttributes();

        if (attributesMap.isEmpty()) {
            return entity;
        }

        return Records.with(entity, attributesMap);
    }
}
