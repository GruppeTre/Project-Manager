package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private EmployeeService employeeService;

    public AccountService(AccountRepository accountRepository, EmployeeService employeeService) {
        this.accountRepository = accountRepository;
        this.employeeService = employeeService;
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
}
