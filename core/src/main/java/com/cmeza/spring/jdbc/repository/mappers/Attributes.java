package com.cmeza.spring.jdbc.repository.mappers;

import java.util.Map;

public interface Attributes {
    Attributes addAttribute(String name, Object value);

    Map<String, Object> getAttributes();
}
