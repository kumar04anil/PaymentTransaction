package com.db.awmd.challenge.exception;

public class ApiException extends SimpleBankingGlobalException{
    public ApiException(String message){
        super(message, GlobalErrorCode.INVALID_TRANSACTION);
    }
}
