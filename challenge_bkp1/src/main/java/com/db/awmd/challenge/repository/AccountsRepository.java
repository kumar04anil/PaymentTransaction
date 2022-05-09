package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.entity.TransactionalEntity;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import javax.transaction.TransactionalException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();

  void createTransactionAccount(TransactionalEntity transactionDetails)throws TransactionalException;

}
