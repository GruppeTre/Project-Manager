package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.EmployeeRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Repository;
import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.exception.PageNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AccountService {

    private final int SUPER_ADMIN_ID = 1;
    private final AccountRepository accountRepository;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public AccountService(AccountRepository accountRepository, EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.accountRepository = accountRepository;
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }

    //Registers a user
    public Account createUser(Account account) {

        trimFields(account);

        Employee checkEmployee = employeeService.getEmployeeByMail(account.getMail());

        if(checkEmployee == null) {
            throw new InvalidFieldException("Employee not found", Field.EMPLOYEE);
        }

        //validate that account does not already exist
        if (getAccountByMail(account.getMail()) != null) {
            throw new InvalidFieldException("An account with that mail already exists!", Field.EMAIL);
        }

        if(!isValidPassword(account.getPassword())) {
            throw new InvalidFieldException("Invalid password", Field.PASSWORD);
        }

        account.setEmployee(checkEmployee);
        account.setPassword(encoder.encode(account.getPassword()));

        try {
            account = accountRepository.createUser(account);
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Account passed all checks but failed to insert!");
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

        //save desired new role in temp variable
        Role newRole = updatedAccount.getRole();

        //Fill out Account data from repository (mainly to get ID from database)
        updatedAccount = getAccountByMail(updatedAccount.getMail());
        //Set the desired role from temp variable
        updatedAccount.setRole(newRole);

        return accountRepository.updatedAccount(updatedAccount);
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

    public List<Account> getAccountsByRole(Role role) {
        return accountRepository.getAccountsByRole(role);
    }

    public List<Account> getAccountsAssignedToTask(int taskId) {
        return accountRepository.getAccountsByTaskId(taskId);
    }


    public Account deleteAccount(Account toDelete) {

        //User should not be able to delete every account
        if (toDelete.getId() == SUPER_ADMIN_ID) {
            throw new IllegalArgumentException("The super admin account cannot be deleted!");
        }

        //insert nullcheck for ID here

        return this.accountRepository.deleteAccount(toDelete);
    }

    public String generatePassword(){
        SecureRandom secureRandom = new SecureRandom();
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String lowerCasePool = "abcdefghijklmnopqrstuvxyz";
        String upperCasePool = "ABCEDFGHIJKLMNOPQRSTUVWXYZ";
        String numberPool = "1234567890";
        String specialCharacterPool = "!#€%&/()=?+-*@${}";

        //Could this be simplified, yes, but it was a nice and easy way to introduce randomly generated password with knowledge on hand.
        for(int i = 0; i < 8; i++){
            int randomIndex = secureRandom.nextInt(lowerCasePool.length());
            sb.append(lowerCasePool.charAt(randomIndex));
        }

        for(int i = 0; i < 4; i++){
            int randomIndex = secureRandom.nextInt(upperCasePool.length());
            int insertIndex = secureRandom.nextInt(sb.length() + 1);
            sb.insert(insertIndex, upperCasePool.charAt(randomIndex));
        }

        for(int i = 0; i < 2; i++){
            int randomIndex = secureRandom.nextInt(numberPool.length());
            int insertIndex = secureRandom.nextInt(sb.length() + 1);
            sb.insert(insertIndex, numberPool.charAt(randomIndex));
        }

        for(int i = 0; i < 2; i++){
            int randomIndex = secureRandom.nextInt(numberPool.length());
            int insertIndex = secureRandom.nextInt(sb.length() + 1);
            sb.insert(insertIndex, specialCharacterPool.charAt(randomIndex));
        }

        return sb.toString();
    }

    private boolean isValidPassword(String str){

        int MIN_LENGTH;

        //todo: Insert password validation here (min amount of characters etc)

        if(containsWhitespace(str)) {
            return false;
        }

        if(str.isBlank()){
            return false;
        }

        return true;
    }

    private boolean containsWhitespace(String str) {
        String regex = "^\\S+$";

        boolean found = str.matches(regex);

        return !found;
    }

    private void trimFields(Account account) {
        account.getEmployee().setMail(account.getMail().trim());
    }
}
