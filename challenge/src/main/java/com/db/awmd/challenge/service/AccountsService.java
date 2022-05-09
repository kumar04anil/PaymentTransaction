package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.AccountValidation;
import com.db.awmd.challenge.exception.ApiException;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.PaymentTransaction;
import com.db.awmd.challenge.entity.TransactionalEntity;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.validate.BankAccountValidation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;
    private Account amountDebited;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    @Autowired
    AccountValidation accountValidation;

    @Autowired
    private EmailNotificationService emailNotificationService;

    public void createAccount(Account account)throws DuplicateAccountIdException{
        if (account.getBalance() != null && !account.getAccountId().isEmpty()) {
            this.accountsRepository.createAccount(account);
        } else {
            throw new DuplicateAccountIdException("Account details not be null");
        }
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public PaymentTransaction paymentTrnx(PaymentTransaction paymentTranx) throws ApiException {
        log.info("Sending fund transfer request {}" + paymentTranx.toString());
        PaymentTransaction paymentTransaction = new PaymentTransaction();

        BigDecimal debitAmt = paymentTranx.getDebitPaymt();

        Account accountDebit = accountsRepository.getAccount(paymentTranx.getSenderAccount());
        Account accountCredit = accountsRepository.getAccount(paymentTranx.getReceiverAccount());

        accountValidation.amountValidation(accountDebit, debitAmt);
        paymentTransaction = paymentProcessing(accountDebit, accountCredit, paymentTranx);
        updatedPaymentMapping(paymentTransaction);

        log.info("transaction audit started");
        return paymentTransaction;
    }

    public PaymentTransaction paymentProcessing(Account accountDebit, Account accountCredited, PaymentTransaction paymentTranx) throws ApiException {
      log.info("payment processing started");
        PaymentTransaction paymentTransaction = new PaymentTransaction();

        log.info("amount is debited from Account");
        accountDebit.setBalance(accountDebit.getBalance().subtract(paymentTranx.getDebitPaymt()));
        paymentTransaction.setDebitPaymt(paymentTranx.getDebitPaymt());

        log.info("amount credited to Account");
        accountCredited.setBalance(accountCredited.getBalance().add(paymentTranx.getDebitPaymt()));
        paymentTransaction.setCreditPaymt(accountCredited.getBalance());

        paymentTransaction.setStatus("success");
        paymentTransaction.setTransactionReference(UUID.randomUUID().toString());
        paymentTransaction.setSenderAccount(accountDebit.getAccountId());
        paymentTransaction.setReceiverAccount(accountCredited.getAccountId());
        log.info("+++++++++++++++++++++++payment done++++++++++++++++++++++");
        return paymentTransaction;
    }

    public void saveTrnx(PaymentTransaction paymentTrnx) {
        log.info("Persist trnx details to DB");
        TransactionalEntity transactionalEntity = new TransactionalEntity();
        transactionalEntity.setTransactionId(paymentTrnx.getTransactionReference());
        transactionalEntity.setSenderAccount(paymentTrnx.getSenderAccount());
        transactionalEntity.setReceiverAccount(paymentTrnx.getReceiverAccount());
        transactionalEntity.setStatus(paymentTrnx.getStatus());
        transactionalEntity.setAmountDebit(paymentTrnx.getDebitPaymt());
        transactionalEntity.setAmountCredit(paymentTrnx.getCreditPaymt());

        this.accountsRepository.createTransactionAccount(transactionalEntity);
        log.info("Trnx persist done");
    }

    public void updatedPaymentMapping(PaymentTransaction paymentTrns) {
        String message = "Transaction id -" + paymentTrns.getTransactionReference();
        paymentTrns.setMessage(message);

        //send notification for Fund transfer
        Account account = new Account(paymentTrns.getReceiverAccount());
        emailNotificationService.notifyAboutTransfer(account, message);
        saveTrnx(paymentTrns);
       // return paymentTransaction;
    }

}
