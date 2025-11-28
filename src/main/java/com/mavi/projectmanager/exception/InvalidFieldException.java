package com.mavi.projectmanager.exception;

public class InvalidFieldException extends RuntimeException {

    private final Field field;

    public InvalidFieldException(String message, Field field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field.getValue();
    }
}
