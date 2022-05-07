package com.db.awmd.challenge.exception;

public class PaymentTrxException extends  SimpleBankingGlobalException{

    public PaymentTrxException(){
        super("Transaction rollback - ", GlobalErrorCode.INVALID_TRANSACTION);
    }
}
