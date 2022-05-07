package com.db.awmd.challenge.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

public class DuplicateAccountIdException extends RuntimeException {

  public DuplicateAccountIdException(String message) {
    super(message);
  }
}
