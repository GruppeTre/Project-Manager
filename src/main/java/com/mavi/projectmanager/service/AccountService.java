package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    //Registers a user
    public Account createAccount(Account account) {

        return accountRepository.createAccount(account);
    }

}
