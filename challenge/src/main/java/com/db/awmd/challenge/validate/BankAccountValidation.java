package com.db.awmd.challenge.validate;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.PaymentTransaction;
import org.springframework.stereotype.Component;

@Component
public class BankAccountValidation {

    public boolean isAccountRequestValid(Account account){
        return account!=null && account.getAccountId()!=null && account.getBalance()!=null;
    }

    public boolean isValidAccount(PaymentTransaction paymentTransaction){
        return paymentTransaction!=null
                && paymentTransaction.getSenderAccount()!=null && paymentTransaction.getReceiverAccount()!=null
                && paymentTransaction.getDebitPaymt()!=null;
    }

    public boolean isAvailableSenderReciverAcc(Account sender, Account reciver){
        return sender.getAccountId()!=null && reciver.getAccountId()!=null
                && sender.getBalance()!=null;
    }

}
