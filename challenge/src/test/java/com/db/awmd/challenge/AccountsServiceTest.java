package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.PaymentTransaction;
import com.db.awmd.challenge.entity.TransactionalEntity;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.PaymentTrxException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;

import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.validate.BankAccountValidation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Autowired
  BankAccountValidation bankAccountValidation;

  @Autowired
  private EmailNotificationService emailNotificationService;

  @org.junit.jupiter.api.Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }

  @Test
  public void accountTrnx_fundTransferService() throws Exception{
    try {
      Account senderAccount = new Account("Id-123");
      senderAccount.setBalance(new BigDecimal(1000));
      this.accountsService.createAccount(senderAccount);
      assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(senderAccount);

      Account receiverAccount = new Account("Id-345");
      receiverAccount.setBalance(new BigDecimal(500));
      this.accountsService.createAccount(receiverAccount);
      assertThat(this.accountsService.getAccount("Id-345")).isEqualTo(receiverAccount);

      PaymentTransaction paymentTransaction = new PaymentTransaction();
      paymentTransaction.setReceiverAccount(receiverAccount.getAccountId());
      paymentTransaction.setSenderAccount(senderAccount.getAccountId());
      paymentTransaction.setDebitPaymt(senderAccount.getBalance());

      this.accountsService.paymentTrnx(paymentTransaction);

    }catch (PaymentTrxException ex) {
      assertThat(ex.getMessage());
    }
  }

  @Test
  public void nullAccountTrnx_fundTransferService() throws Exception{
    try {
      Account senderAccount = new Account("");
      senderAccount.setBalance(new BigDecimal(1000));
      this.accountsService.createAccount(senderAccount);
      assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(senderAccount);

      Account receiverAccount = new Account("Id-345");
      receiverAccount.setBalance(new BigDecimal(500));
      this.accountsService.createAccount(receiverAccount);
      assertThat(this.accountsService.getAccount("Id-345")).isEqualTo(receiverAccount);

      PaymentTransaction paymentTransaction = new PaymentTransaction();
      paymentTransaction.setReceiverAccount(receiverAccount.getAccountId());
      paymentTransaction.setSenderAccount(senderAccount.getAccountId());
      paymentTransaction.setDebitPaymt(senderAccount.getBalance());

      this.accountsService.paymentTrnx(paymentTransaction);

    }catch (PaymentTrxException ex) {
      assertThat(ex.getMessage());
    }
  }

  @Test
  public void saveTrnx(){
    try{
      Account senderAccount = new Account("Id-123");
      senderAccount.setBalance(new BigDecimal(1000));
      this.accountsService.createAccount(senderAccount);
      assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(senderAccount);

      Account receiverAccount = new Account("Id-345");
      receiverAccount.setBalance(new BigDecimal(500));
      this.accountsService.createAccount(receiverAccount);
      assertThat(this.accountsService.getAccount("Id-345")).isEqualTo(receiverAccount);

      PaymentTransaction paymentTransaction = new PaymentTransaction();
      paymentTransaction.setReceiverAccount(receiverAccount.getAccountId());
      paymentTransaction.setSenderAccount(senderAccount.getAccountId());
      paymentTransaction.setDebitPaymt(senderAccount.getBalance());
      paymentTransaction.setStatus("success");
      paymentTransaction.setTransactionReference("TRNX12334");

      paymentTransaction = accountsService.paymentTrnx(paymentTransaction);

      this.accountsService.saveTrnx(paymentTransaction);
    }catch (Exception ex){
      assertThat(ex.getMessage());
    }
  }

  @Test
  public void updatedPaymentMapping(){
    try{
      Account senderAccount = new Account("Id-123");
      senderAccount.setBalance(new BigDecimal(1000));
      this.accountsService.createAccount(senderAccount);
      assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(senderAccount);

      Account receiverAccount = new Account("Id-345");
      receiverAccount.setBalance(new BigDecimal(500));
      this.accountsService.createAccount(receiverAccount);
      assertThat(this.accountsService.getAccount("Id-345")).isEqualTo(receiverAccount);

      PaymentTransaction paymentTransaction = new PaymentTransaction();
      paymentTransaction.setReceiverAccount(receiverAccount.getAccountId());
      paymentTransaction.setSenderAccount(senderAccount.getAccountId());
      paymentTransaction.setDebitPaymt(senderAccount.getBalance());
      paymentTransaction.setStatus("success");
      paymentTransaction.setTransactionReference("TRNX12334");

      paymentTransaction = accountsService.paymentTrnx(paymentTransaction);

      String message = "Transaction id -" + paymentTransaction.getTransactionReference() + " Status " + paymentTransaction.getStatus();
      paymentTransaction.setMessage(message);

      Account account = new Account(receiverAccount.getAccountId());
      //send notification for Fund transfer
      emailNotificationService.notifyAboutTransfer(account, message);
      this.accountsService.updatedPaymentMapping(paymentTransaction, senderAccount, receiverAccount);
    }catch (Exception ex){
      assertThat(ex.getMessage());
    }
  }
}
