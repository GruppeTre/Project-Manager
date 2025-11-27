package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Repository;
import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.exception.PageNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final EmployeeService employeeService;
    private final Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public AccountService(AccountRepository accountRepository, EmployeeService employeeService) {
        this.accountRepository = accountRepository;
        this.employeeService = employeeService;
    }
  
    public boolean isValidPassword(String str){
        if(str.isEmpty()){
            throw new InvalidFieldException("Password cannot be empty", Field.PASSWORD);
        } else return true;
    }

    //Registers a user
    public Account createUser(Account account) {

        Employee checkEmployee = employeeService.getEmployeeByMail(account.getMail());

        if(checkEmployee == null) {
            throw new RuntimeException();
        }

        account.setEmployee(checkEmployee);

        if(isValidPassword(account.getPassword())){
            try {
                accountRepository.createUser(account);
            }
            catch (RuntimeException e) {
                System.out.println("Failed to insert");
            }
        }
        return account;
    }

    public Account getAccountByID(int id){
        Account account = accountRepository.getAccountByID(id);

        if(account == null){
            throw new PageNotFoundException("The account with id: " + id + " does not exist!");
        }

        return account;
    }

    public Account getAccountByMail(String mail){
        return accountRepository.getAccountByMail(mail);
    }

    public Account updatedAccount(Account updatedAccount){
        if (isValidPassword(updatedAccount.getPassword())) {
            return accountRepository.updatedAccount(updatedAccount);
        }
        else {
            return null;
        }
    }

    //Get all accounts stored in a List
    public List<Account> getAccounts() {
        return accountRepository.getAccounts();
    }

    public boolean accountLogin(Account account){
        try {
        Account getAccount = accountRepository.getAccountByMail(account.getMail());

            return encoder.matches(account.getPassword(), getAccount.getPassword());
        } catch (RuntimeException e) {
            return false;
        }
    }
}
