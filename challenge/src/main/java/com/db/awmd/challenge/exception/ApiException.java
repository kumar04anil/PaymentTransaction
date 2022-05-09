package com.db.awmd.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiException extends Exception{
    private String message;
    private HttpStatus status;
    private Exception ex;

    public ApiException(String message){
        this.message=message;
    }

    public ApiException(HttpStatus status, String message, Exception ex){
        this.status = status;
        this.message = message;
        this.ex = ex;
    }

    public ApiException(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
