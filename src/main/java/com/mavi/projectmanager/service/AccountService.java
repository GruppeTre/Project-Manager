package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.exception.PageNotFoundException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public boolean isValidPassword(String str){
        if(str.isEmpty()){
            throw new InvalidFieldException("Password cannot be empty", Field.PASSWORD);
        } else return true;
    }

    public Account getAccountByID(int id){
        Account account = accountRepository.getAccountByID(id);
gt
        if(account == null){
            throw new PageNotFoundException("The account with id: " + id + " does not exist!");
        }

        return account;
    }

    public Account updatedAccount(Account updatedAccount){
        if (isValidPassword(updatedAccount.getPassword())) {
            return accountRepository.updatedAccount(updatedAccount);
        }
        else {
            return null;
        }
    }
}
