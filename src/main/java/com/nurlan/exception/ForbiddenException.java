package com.nurlan.exception;

public class ForbiddenException extends BaseException {
    public ForbiddenException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
