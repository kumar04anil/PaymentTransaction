package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.ApiException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.validate.BankAccountValidation;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class AccountValidation {

    @Autowired
    BankAccountValidation bankAccountValidation;

    public void accountValidation(PaymentTransaction paymentTranx) throws ApiException {
        log.info("account validation...");
        PaymentTransaction paymentTransaction = null;
        if (!bankAccountValidation.isDebitPaymtAvl(paymentTranx))
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "please provide debiting amount");
        else if (!bankAccountValidation.isValidSenderAcc(paymentTranx))
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "invalid sender account");
        else if (!bankAccountValidation.isValidRecvAcc(paymentTranx))
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "invalid receiver account");

        log.info("account validation done...");
        // return true;
    }

    public void amountValidation(Account senderAcc, BigDecimal debitAmt) throws ApiException {
        log.info("amount validation started...");
        PaymentTransaction paymentTransaction = null;

        BigDecimal dbTotal = senderAcc.getBalance();
        BigDecimal db = debitAmt;
        double totalAmount = dbTotal.doubleValue();
        double debatingAmount = db.doubleValue();

        if (!bankAccountValidation.isvalidTotalAmt(totalAmount, debatingAmount)) {
            log.info("Invalid account details or amount");
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE,"Insufficient balance");
        }
        log.info("amount validation done...");
    }
}
