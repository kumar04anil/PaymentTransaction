package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountValidation;
import com.db.awmd.challenge.domain.PaymentTransaction;
import com.db.awmd.challenge.exception.ApiException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.validate.BankAccountValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountValidationTest {
    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    @Lazy
    AccountValidation accountValidation;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
        // Reset the existing accounts before each test.
        accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    public void accountValidationTest() throws Exception {
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
            BigDecimal debitAmt = BigDecimal.valueOf(500);
            paymentTransaction.setDebitPaymt(debitAmt);

            this.accountValidation.accountValidation(paymentTransaction);
        }catch (Exception ex){
            ex.getMessage();
        }
    }
    @Test
    public void accountValidationFailedTest() throws Exception {
        try {
            Account senderAccount = new Account("");
            senderAccount.setBalance(new BigDecimal(0));
            this.accountsService.createAccount(senderAccount);
            assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(senderAccount);

            Account receiverAccount = new Account("Id-345");
            receiverAccount.setBalance(new BigDecimal(500));
            this.accountsService.createAccount(receiverAccount);
            assertThat(this.accountsService.getAccount("Id-345")).isEqualTo(receiverAccount);

            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setReceiverAccount(receiverAccount.getAccountId());
            paymentTransaction.setSenderAccount(senderAccount.getAccountId());
            BigDecimal debitAmt = BigDecimal.valueOf(500);
            paymentTransaction.setDebitPaymt(debitAmt);

            this.accountValidation.accountValidation(paymentTransaction);
        }catch (Exception ex){
            ex.getMessage();
        }
    }

    @Test
    public void amountValidation() throws Exception {
        Account senderAccount = new Account("");
        senderAccount.setBalance(new BigDecimal(500));
        this.accountsService.createAccount(senderAccount);
        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(senderAccount);
        BigDecimal debitAmt = BigDecimal.valueOf(500);

        try {
            this.accountValidation.amountValidation(senderAccount, debitAmt);
        } catch (ApiException ex) {
            assertThat(ex.getMessage());
        }
    }
    @Test
    public void amountValidationFailTest() throws Exception {
        Account senderAccount = new Account("");
        senderAccount.setBalance(new BigDecimal(500));
        this.accountsService.createAccount(senderAccount);
        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(senderAccount);
        BigDecimal debitAmt = BigDecimal.valueOf(5000);

        try {
            this.accountValidation.amountValidation(senderAccount, debitAmt);
        } catch (ApiException ex) {
            assertThat(ex.getMessage());
        }
    }


}
