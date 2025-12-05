package com.mavi.projectmanager.exception;

import com.mavi.projectmanager.model.Role;

public class InvalidDateException extends InvalidFieldException {

    private int errorId;

    public InvalidDateException(String message, int errorId) {
        super(message, Field.DATE);
        this.errorId = errorId;
    }

    public int getErrorId() {
        return errorId;
    }
}
