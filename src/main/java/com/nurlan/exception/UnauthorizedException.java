package com.nurlan.exception;

public class UnauthorizedException extends BaseException{
    public UnauthorizedException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
