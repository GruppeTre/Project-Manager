package com.mavi.projectmanager.exception;

public enum Field {
    EMAIL("email"),
    FIRST_NAME("firstName"),
    SURNAME("surname"),
    PASSWORD("password");

    private final String value;

    private Field(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
