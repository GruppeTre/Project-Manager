package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.exception.PageNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    //Registers a user
    //Magnus Sørensen
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

    //Jens Gotfredsen
    public Account getAccountByID(int id){
        Account account = accountRepository.getAccountByID(id);

        if(account == null){
            throw new PageNotFoundException("The account with id: " + id + " does not exist!");
        }

        return account;
    }


    //Jens Gotfredsen
    public Account getAccountByMail(String mail){
        return accountRepository.getAccountByMail(mail);
    }

    //Jens Gotfredsen
    public Account updatedAccount(Account updatedAccount){

        //save desired new role in temp variable
        Role newRole = updatedAccount.getRole();
        String newPassword = updatedAccount.getPassword();
        //Fill out Account data from repository (mainly to get ID from database)
        updatedAccount = getAccountByMail(updatedAccount.getMail());
        //Set the desired role from temp variable
        updatedAccount.setRole(newRole);

        //Checks if the new password is not null or empty
        if (newPassword != null && !newPassword.isEmpty()) {

            //Checks if the new password matches the old password
            if (!encoder.matches(newPassword, updatedAccount.getPassword())) {

                String hashedPassword = encoder.encode(newPassword);
                updatedAccount.setPassword(hashedPassword);
            }
        }

        return accountRepository.updatedAccount(updatedAccount);
    }

    //Get all accounts stored in a List
    //Jens Gotfredsen
    public List<Account> getAccounts() {
        return accountRepository.getAccounts();
    }


    //Magnus Sørensen
    public boolean accountLogin(Account account){
        try {
        Account getAccount = accountRepository.getAccountByMail(account.getMail());

            return encoder.matches(account.getPassword(), getAccount.getPassword());
        } catch (RuntimeException e) {
            return false;
        }
    }

    //Jacob Klitgaard
    public List<Account> getAccountsByRole(Role role) {
        return accountRepository.getAccountsByRole(role);
    }

    //Magnus Sørensen
    public List<Account> getAccountsAssignedToTask(int taskId) {
        return accountRepository.getAccountsByTaskId(taskId);
    }

    //Magnus Sørensen
    public Account deleteAccount(Account toDelete) {
        final int SUPER_ADMIN_ID = 1;

        //User should not be able to delete every account
        if (toDelete.getId() == SUPER_ADMIN_ID) {
            throw new IllegalArgumentException("The super admin account cannot be deleted!");
        }

        //insert nullcheck for ID here

        return this.accountRepository.deleteAccount(toDelete);
    }

    //Jens Gotfredsen
    public String generatePassword() {
        final String LOWER_CASE_POOL = "abcdefghijklmnopqrstuvxyz";
        final String UPPER_CASE_POOL = "ABCEDFGHIJKLMNOPQRSTUVWXYZ";
        final String NUMBER_POOL = "1234567890";
        final String SPECIAL_CHAR_POOL = "!#€%&/()=?+-*@${}";
        final int PASSWORD_LENGTH = 16;

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        //Appends 8 secure random lowercase characters in string builder
        for(int i = 0; i < PASSWORD_LENGTH/2; i++){
            int randomIndex = secureRandom.nextInt(LOWER_CASE_POOL.length());
            sb.append(LOWER_CASE_POOL.charAt(randomIndex));
        }

        //Inserts 4 secure random upper case characters to string builder
        for(int i = 0; i < PASSWORD_LENGTH/4; i++){
            int randomIndex = secureRandom.nextInt(UPPER_CASE_POOL.length());
            int insertIndex = secureRandom.nextInt(sb.length() + 1);
            sb.insert(insertIndex, UPPER_CASE_POOL.charAt(randomIndex));
        }

        //Inserts 2 secure random numbers in string builder
        for(int i = 0; i < PASSWORD_LENGTH/8; i++){
            int randomIndex = secureRandom.nextInt(NUMBER_POOL.length());
            int insertIndex = secureRandom.nextInt(sb.length() + 1);
            sb.insert(insertIndex, NUMBER_POOL.charAt(randomIndex));
        }

        //Inserts 2 secure random special characters in string builder
        for(int i = 0; i < PASSWORD_LENGTH/8; i++){
            int randomIndex = secureRandom.nextInt(NUMBER_POOL.length());
            int insertIndex = secureRandom.nextInt(sb.length() + 1);
            sb.insert(insertIndex, SPECIAL_CHAR_POOL.charAt(randomIndex));
        }

        return sb.toString();
    }

    //Magnus Sørensen
    private boolean isValidPassword(String password){

        final int MIN_LENGTH = 16;

        if(password.length() < MIN_LENGTH) {
            return false;
        }

        if(containsWhitespace(password)) {
            return false;
        }

        return !password.isBlank();
    }

    //Magnus Sørensen
    private boolean containsWhitespace(String str) {
        String regex = "^\\S+$";

        boolean found = str.matches(regex);

        return !found;
    }

    //Magnus Sørensen
    private void trimFields(Account account) {
        account.getEmployee().setMail(account.getMail().trim());
    }
}
