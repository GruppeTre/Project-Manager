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
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldCreateUser() {

        Account newAccount = new Account();
        newAccount.setPassword("1234");
        newAccount.setRole(Role.ADMIN);

        Employee employee = new Employee();
        employee.setId(2);
        newAccount.setEmployee(employee);

        Account account = repository.createUser(newAccount, employee);

        assertThat(account).isNotNull();
        assertThat(account.getPassword()).isEqualTo("1234");
        assertThat(account.getRole()).isEqualTo(Role.ADMIN);
    }
}