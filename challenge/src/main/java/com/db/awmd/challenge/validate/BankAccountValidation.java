package com.db.awmd.challenge.validate;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.PaymentTransaction;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Component
@Service
public class BankAccountValidation {

    public boolean isAccountRequestValid(Account account){
        return account!=null && account.getAccountId()!=null && account.getBalance()!=null;
    }

    public boolean isValidSenderAcc(PaymentTransaction paymentTransaction){
        return paymentTransaction!=null
                && paymentTransaction.getSenderAccount()!=null && !paymentTransaction.getSenderAccount().isEmpty();
    }

    public boolean isValidRecvAcc(PaymentTransaction paymentTransaction){
        return paymentTransaction!=null && paymentTransaction.getReceiverAccount()!=null && !paymentTransaction.getReceiverAccount().isEmpty();
    }

    public boolean isDebitPaymtAvl(PaymentTransaction paymentTransaction){
        return paymentTransaction.getDebitPaymt().compareTo(BigDecimal.ZERO) >= 0;

    }

    public boolean isValidAccount(String sender, String receiver){
        return sender!=null && !sender.isEmpty()  && receiver!=null&& !receiver.isEmpty();
    }

    public boolean isvalidTotalAmt(double totalAmount, double debatingAmount){
        return totalAmount!=0 && debatingAmount!=0 && totalAmount >= debatingAmount;
    }

}
