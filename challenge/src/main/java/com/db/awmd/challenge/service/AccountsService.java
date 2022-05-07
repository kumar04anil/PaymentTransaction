package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.PaymentTransaction;
import com.db.awmd.challenge.entity.TransactionalEntity;
import com.db.awmd.challenge.exception.*;
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
  BankAccountValidation bankAccountValidation;

  @Autowired
  private EmailNotificationService emailNotificationService;

  public void createAccount(Account account) {

    if (account.getBalance() != null && !account.getAccountId().isEmpty()) {
      this.accountsRepository.createAccount(account);
    } else {
      throw new ApiException("Account details not be null");
    }
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  @Transactional(rollbackOn = RuntimeException.class)
  public PaymentTransaction paymentTrnx(PaymentTransaction paymentTranx) {
    log.info("Sending fund transfer request {}" + paymentTranx.toString());

    PaymentTransaction paymentTransaction = new PaymentTransaction();
    Account amountDebited = new Account(paymentTranx.getSenderAccount());
    Account amountCredited = null;
    BigDecimal creditAmt;
    BigDecimal debtAmt;

    if (bankAccountValidation.isValidAccount(paymentTranx)) {
      amountDebited = accountsRepository.getAccount(paymentTranx.getSenderAccount());
      amountCredited = accountsRepository.getAccount(paymentTranx.getReceiverAccount());

      if (!amountDebited.getAccountId().isEmpty() && !amountCredited.getAccountId().isEmpty() && amountDebited.getBalance() != null) {

        BigDecimal bd = amountDebited.getBalance();
        BigDecimal bd1 = paymentTranx.getDebitPaymt();
        double totalAmount = bd.doubleValue();
        double debatingAmount = bd1.doubleValue();

        if (totalAmount != 0 && totalAmount >= debatingAmount) {
          debtAmt = amountDebited.getBalance().subtract(paymentTranx.getDebitPaymt());
          amountDebited.setBalance(debtAmt);
          paymentTransaction.setDebitPaymt(paymentTranx.getDebitPaymt());
          log.info("Amount :" + paymentTranx.getDebitPaymt() + " debited from Account " + amountDebited.getAccountId());

          creditAmt = amountCredited.getBalance().add(paymentTranx.getDebitPaymt());
          amountCredited.setBalance(creditAmt);
          paymentTransaction.setCreditPaymt(creditAmt);
          log.info("credited :" + paymentTranx.getDebitPaymt() + "  to Account " + amountCredited.getAccountId() + " TotalBalance is :" + creditAmt);

          paymentTransaction.setStatus("success");
        } else throw new SimpleBankingGlobalException("InSufficient Amount","400");
      } else {
        log.info("Invalid account details or amount SenderAccountId:{}, ReceiverAccountId: {}, AmountToDebit{}", amountDebited.getAccountId(), amountCredited.getAccountId(), amountDebited.getBalance());
        paymentTransaction.setStatus("failed");
        paymentTransaction.setMessage("Payment processing failed null value not allowed");
        throw new PaymentTrxException();
      }
    } else {
      log.info("invalid account details");
      paymentTransaction.setStatus("failed");
      paymentTransaction.setMessage("Payment processing failed null value not allowed");
    }
    paymentTranx = updatedPaymentMapping(paymentTransaction, amountDebited, amountCredited);
    saveTrnx(paymentTranx);
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
    transactionalEntity.setTotalAmountCredit(paymentTrnx.getCreditPaymt());

    this.accountsRepository.createTransactionAccount(transactionalEntity);
    log.info("Trnx persist done");
  }

  public PaymentTransaction updatedPaymentMapping(PaymentTransaction paymentTransactions, Account senderAcc, Account receiverAcc) {
    paymentTransactions.setTransactionReference(UUID.randomUUID().toString());
    paymentTransactions.setSenderAccount(senderAcc.getAccountId());
    paymentTransactions.setReceiverAccount(receiverAcc.getAccountId());
    String message = "Transaction id -" + paymentTransactions.getTransactionReference();
    paymentTransactions.setMessage(message);

    Account account = new Account(receiverAcc.getAccountId());
    //send notification for Fund transfer
    emailNotificationService.notifyAboutTransfer(account, message);
    return paymentTransactions;
  }

}
