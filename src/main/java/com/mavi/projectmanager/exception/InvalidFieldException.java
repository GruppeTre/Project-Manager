package com.mavi.projectmanager.exception;

public class InvalidFieldException extends RuntimeException {

    private Field field;

    public InvalidFieldException(String message, Field field) {
        super(message);
    }

    public Field getField() {
        return field;
    }
}
