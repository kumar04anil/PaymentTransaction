package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.PaymentTransaction;
import com.db.awmd.challenge.exception.ApiException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.validate.BankAccountValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.UnexpectedTypeException;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts/")
@Slf4j
public class PaymentController {
    private final AccountsService accountsService;

    @Autowired
    BankAccountValidation bankAccountValidation;

    @Autowired
    public PaymentController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @PostMapping(value="/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> paymentTrnx(@RequestBody @Valid PaymentTransaction paymentTransaction) throws ApiException {
        log.info("Money Transfer to account {}", paymentTransaction.getSenderAccount());
        try {
            paymentTransaction = accountsService.paymentTrnx(paymentTransaction);
        } catch (ApiException apiEx) {
            return new ResponseEntity<>(apiEx.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnexpectedTypeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        log.info("Transaction completed for account {}", paymentTransaction.getSenderAccount());
        return ResponseEntity.ok(paymentTransaction);
    }
}
