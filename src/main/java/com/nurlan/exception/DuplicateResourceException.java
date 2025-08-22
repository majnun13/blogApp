package com.nurlan.exception;

public class DuplicateResourceException extends BaseException {
    public DuplicateResourceException(ErrorMessage errorMessage) {
        super(errorMessage);

    }
}