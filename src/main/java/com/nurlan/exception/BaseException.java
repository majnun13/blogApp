package com.nurlan.exception;

public class BaseException extends RuntimeException{


    public BaseException(ErrorMessage message){
        super(message.prepareErrorMessage());
    }

    public BaseException() {
    }
}
