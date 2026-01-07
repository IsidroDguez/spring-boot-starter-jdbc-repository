package com.cmeza.spring.jdbc.repository.mappers.records;

import com.cmeza.spring.jdbc.repository.mappers.Attributes;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecordsAttributesImpl implements Attributes {
    private final Map<String, Object> map = new LinkedHashMap<>();

    @Override
    public Attributes addAttribute(String name, Object value) {
        map.put(name, value);
        return this;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return map;
    }
}
