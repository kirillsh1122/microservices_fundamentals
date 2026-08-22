package com.microservice.architecture.overview.storage_service.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum STORAGE_ENTRY_TYPE {

    STAGING("STAGING"),
    PERMANENT("PERMANENT"),
    DEFAULT("DEFAULT");

    private final String value;

    STORAGE_ENTRY_TYPE(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static STORAGE_ENTRY_TYPE fromValue(String text) {
        for (STORAGE_ENTRY_TYPE b : STORAGE_ENTRY_TYPE.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
