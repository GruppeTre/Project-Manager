package com.mavi.projectmanager.exception;

public enum Field {
    EMAIL("email"),
    FIRST_NAME("firstName"),
    SURNAME("surname"),
    PASSWORD("password"),
    EMPLOYEE("employee");

    private final String value;

    Field(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
