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
        if(id == accountRepository.getAccountByID(id).getId()) {
            return accountRepository.getAccountByID(id);
        } else {
            throw new PageNotFoundException("Account with ID:" + id + " do not exist");
        }
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
