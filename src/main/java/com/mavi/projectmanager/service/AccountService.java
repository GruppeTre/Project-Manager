package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.stereotype.Repository;
import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.exception.PageNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private EmployeeService employeeService;

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
    public Account createUser(Account account, String mail) {

        Employee checkEmployee = employeeService.getEmployeeByMail(mail);

        if(checkEmployee == null) {
            throw new RuntimeException();
        }

        if(isValidPassword(account.getPassword())){
            try {
                accountRepository.createUser(account, checkEmployee);
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

    public Account getAccountByMail(Account account, String mail){
        return accountRepository.getAccountByEmployeeMail(account, mail);
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

    public boolean accountLogin(Employee employee, Account account){
        Employee getEmployee = employeeService.getEmployeeByMail(employee.getMail());
        Account getAccount = accountRepository.getAccountByEmployeeID(account, getEmployee.getId());

        if(getAccount == null){
            throw new InvalidFieldException("Password is incorrect", Field.PASSWORD);
        }

        return true;
    }
}
