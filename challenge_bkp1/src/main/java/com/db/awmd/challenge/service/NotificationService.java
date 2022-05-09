package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;

public interface NotificationService {

  void notifyAboutTransfer(Account account, String transferDescription);
}
