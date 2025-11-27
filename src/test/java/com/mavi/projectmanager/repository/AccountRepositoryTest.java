package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;
    private Account newAccount;
    private Employee empWithAccount;
    private Employee empWithoutAcc;

    @BeforeEach
    void setUp() {
        newAccount = new Account();

        empWithoutAcc = new Employee();
        empWithoutAcc.setPosition("Udvikler");
        empWithoutAcc.setFirstName("Ida");
        empWithoutAcc.setLastName("Sørensen");
        empWithoutAcc.setMail("idso@alphasolutions.com");
        empWithoutAcc.setId(2);

        empWithAccount = new Employee();
        empWithAccount.setPosition("Manager");
        empWithAccount.setFirstName("Anders");
        empWithAccount.setLastName("Nielsen");
        empWithAccount.setMail("admin@alphasolutions.com");
        empWithAccount.setId(1);
    }

    @Test
    void shouldCreateUser() {

        String password = "1234";

        Role role = Role.ADMIN;

        newAccount.setPassword(password);
        newAccount.setRole(role);

        newAccount.setEmployee(empWithoutAcc);

        Account account = repository.createUser(newAccount);

        assertNotNull(account);
        assertEquals(password, account.getPassword());
        assertEquals(role, account.getRole());
        assertEquals(empWithoutAcc.getFirstName(), account.getFirstName());
        assertEquals(empWithoutAcc.getLastName(), account.getLastName());
        assertEquals(empWithoutAcc.getMail(), account.getMail());
    }
}