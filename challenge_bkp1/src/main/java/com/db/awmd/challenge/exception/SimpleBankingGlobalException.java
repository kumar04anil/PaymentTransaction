package com.db.awmd.challenge.exception;

import lombok.Data;

@Data
public class SimpleBankingGlobalException extends RuntimeException{
    private String code;
    private String message;

    public SimpleBankingGlobalException(String message, String errorEntityNotFound){
        super(message);
    }

}
