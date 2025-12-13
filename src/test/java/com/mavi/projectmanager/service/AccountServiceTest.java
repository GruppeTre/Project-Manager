package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @MockitoBean
    AccountRepository repository;

    @MockitoBean
    EmployeeService employeeService;

    private final Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private Account logInAccount;
    private Account registerAccount;
    private Account dbAccount;
    private Employee logInEmployee;
    private Employee dbEmployee;

    @BeforeEach
    void setUp() {
        String mail = "test@alphasolutions.com";
        String password = "test123";
        String passwordHash = encoder.encode(password);
        String firstName = "Erik";
        String lastName = "Eriksen";
        String position = "Manager";
        Role role = Role.ADMIN;
        int id = 1;

        logInEmployee = new Employee();
        logInEmployee.setMail(mail);

        logInAccount = new Account();
        logInAccount.setPassword(password);
        logInAccount.setEmployee(logInEmployee);

        registerAccount = new Account();
        registerAccount.setPassword(password);
        registerAccount.setRole(role);
        registerAccount.setEmployee(logInEmployee);

        dbEmployee = new Employee();
        dbEmployee.setMail(mail);
        dbEmployee.setPosition(position);
        dbEmployee.setFirstName(firstName);
        dbEmployee.setLastName(lastName);
        dbEmployee.setId(id);

        dbAccount = new Account();
        dbAccount.setPassword(passwordHash);
        dbAccount.setEmployee(dbEmployee);
        dbAccount.setRole(role);
        dbAccount.setId(id);
    }

    //Magnus Sørensen
    @Test
    void logInShouldReturnTrueOnValidLogin() {
        when(repository.getAccountByMail(logInAccount.getMail())).thenReturn(dbAccount);

        assertTrue(this.accountService.accountLogin(logInAccount));
    }

    //Magnus Sørensen
    @Test
    void logInShouldReturnFalseOnWrongPassword() {
        String wrongPassword = "wrong";

        logInAccount.setPassword(wrongPassword);

        when(repository.getAccountByMail(logInAccount.getMail())).thenReturn(dbAccount);

        assertFalse(this.accountService.accountLogin(logInAccount));
    }

    //Magnus Sørensen
    @Test
    void logInShouldReturnFalseOnWrongMail() {
        String wrongMail = "wrong@wrong.com";

        logInAccount.getEmployee().setMail(wrongMail);

        when(repository.getAccountByMail(logInAccount.getMail())).thenReturn(null);

        assertFalse(this.accountService.accountLogin(logInAccount));
    }

    //Magnus Sørensen
    @Test
    void createUserShouldAcceptValidUser() {

        when(employeeService.getEmployeeByMail(registerAccount.getMail())).thenReturn(dbEmployee);

        when(repository.createUser(any(Account.class))).thenReturn(dbAccount);

        assertEquals(dbAccount, accountService.createUser(registerAccount));
    }

    //Magnus Sørensen
    @Test
    void createUserShouldRejectInvalidMail() {

        String wrongMail = "wrong@wrong.com";

        registerAccount.getEmployee().setMail(wrongMail);

        when(employeeService.getEmployeeByMail(registerAccount.getMail())).thenReturn(null);

        Throwable exception = assertThrows(InvalidFieldException.class, () -> accountService.createUser(registerAccount));

        assertEquals("Employee not found", exception.getMessage());
    }

    //Magnus Sørensen
    @Test
    void createUserShouldRejectDuplicateMail() {

        String duplicateMail = "already@exists.com";

        registerAccount.getEmployee().setMail(duplicateMail);

        when(employeeService.getEmployeeByMail(registerAccount.getMail())).thenReturn(dbEmployee);

        when(repository.getAccountByMail(registerAccount.getMail())).thenReturn(dbAccount);

        Throwable exception = assertThrows(InvalidFieldException.class, () -> accountService.createUser(registerAccount));

        assertEquals("An account with that mail already exists!", exception.getMessage());
    }

    //Magnus Sørensen
    @Test
    void createUserShouldRejectPasswordWithWhitespace() {

        String invalidPassword = "contains whitespace";

        registerAccount.setPassword(invalidPassword);

        when(employeeService.getEmployeeByMail(registerAccount.getMail())).thenReturn(dbEmployee);

        Throwable exception = assertThrows(InvalidFieldException.class, () -> accountService.createUser(registerAccount));

        assertEquals("Invalid password", exception.getMessage());
    }

    //Magnus Sørensen
    @Test
    void createUserShouldRejectBlankPassword() {

        String invalidPassword = "   ";

        registerAccount.setPassword(invalidPassword);

        when(employeeService.getEmployeeByMail(registerAccount.getMail())).thenReturn(dbEmployee);

        Throwable exception = assertThrows(InvalidFieldException.class, () -> accountService.createUser(registerAccount));

        assertEquals("Invalid password", exception.getMessage());
    }

    //Magnus Sørensen
    @Test
    void createUserShouldRejectEmptyPassword() {

        String invalidPassword = "";

        registerAccount.setPassword(invalidPassword);

        when(employeeService.getEmployeeByMail(registerAccount.getMail())).thenReturn(dbEmployee);

        Throwable exception = assertThrows(InvalidFieldException.class, () -> accountService.createUser(registerAccount));

        assertEquals("Invalid password", exception.getMessage());
    }

    //Magnus Sørensen
    @Test
    void shouldDeleteAccount() {

        dbAccount.setId(2);

        when(repository.deleteAccount(dbAccount)).thenReturn(dbAccount);

        assertNotNull(this.accountService.deleteAccount(dbAccount));
    }

    //Magnus Sørensen
    @Test
    void shouldNotDeleteSuperAdmin() {

        when(repository.deleteAccount(dbAccount)).thenReturn(dbAccount);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> this.accountService.deleteAccount(dbAccount));

        assertEquals("The super admin account cannot be deleted!", exception.getMessage());
    }
}