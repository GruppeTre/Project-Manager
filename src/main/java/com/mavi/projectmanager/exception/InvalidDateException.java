package com.mavi.projectmanager.exception;

public class InvalidDateException extends InvalidFieldException {

    private final int errorId;

    public InvalidDateException(String message, int errorId) {
        super(message, Field.DATE);
        this.errorId = errorId;
    }

    public int getErrorId() {
        return errorId;
    }
}
