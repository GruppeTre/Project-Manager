package com.mavi.projectmanager.service;


import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //Get all accounts stored in a List
    public List<Account> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }
}
