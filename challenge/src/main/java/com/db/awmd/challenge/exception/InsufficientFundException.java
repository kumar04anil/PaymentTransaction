package com.db.awmd.challenge.exception;

public class InsufficientFundException extends  SimpleBankingGlobalException{

    public InsufficientFundException(String message){
        super(message, GlobalErrorCode.INSUFFICIENT_FUNDS);
    }
}
