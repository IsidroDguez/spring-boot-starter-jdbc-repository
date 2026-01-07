package com.cmeza.spring.jdbc.repository.mappers.records.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomDataClassRowMapper<T> extends BeanPropertyRowMapper<T> {
    private Constructor<T> mappedConstructor;
    private String[] constructorParameterNames;
    private TypeDescriptor[] constructorParameterTypes;

    public CustomDataClassRowMapper(Class<T> mappedClass) {
        super(mappedClass);
    }

    public static <T> CustomDataClassRowMapper<T> newInstance(Class<T> mappedClass) {
        return new CustomDataClassRowMapper<>(mappedClass);
    }

    @Override
    protected void initialize(Class<T> mappedClass) {
        super.initialize(mappedClass);
        this.mappedConstructor = BeanUtils.getResolvableConstructor(mappedClass);
        int paramCount = this.mappedConstructor.getParameterCount();
        if (paramCount > 0) {
            this.constructorParameterNames = BeanUtils.getParameterNames(this.mappedConstructor);

            for (String name : this.constructorParameterNames) {
                this.suppressProperty(name);
            }

            this.constructorParameterTypes = new TypeDescriptor[paramCount];

            for (int i = 0; i < paramCount; ++i) {
                this.constructorParameterTypes[i] = new TypeDescriptor(new MethodParameter(this.mappedConstructor, i));
            }
        }

    }

    @Override
    protected T constructMappedInstance(ResultSet rs, TypeConverter tc) throws SQLException {
        Assert.state(this.mappedConstructor != null, "Mapped constructor was not initialized");
        Object[] args;
        if (this.constructorParameterNames != null && this.constructorParameterTypes != null) {
            args = new Object[this.constructorParameterNames.length];

            for (int i = 0; i < args.length; ++i) {
                String name = this.constructorParameterNames[i];

                int index = this.findColumnIndex(rs, name);
                
                if (index == -1) {
                    args[i] = null;
                } else {
                    TypeDescriptor td = this.constructorParameterTypes[i];
                    Object value = this.getColumnValue(rs, index, td.getType());
                    args[i] = tc.convertIfNecessary(value, td.getType(), td);
                }
            }
        } else {
            args = new Object[0];
        }

        return BeanUtils.instantiateClass(this.mappedConstructor, args);
    }

    private int findColumnIndex(ResultSet rs, String name) {
        try {
            return rs.findColumn(this.lowerCaseName(name));
        } catch (SQLException var9) {
            try {
                return rs.findColumn(this.underscoreName(name));
            } catch (SQLException var10) {
                return  -1;
            }
        }
    }
}
