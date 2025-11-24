package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Inserts an account in the database
    public Account createUser(Account account) {

        String query = "INSERT IGNORE INTO account (id, role, password, emp_id) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, account.getId());
                ps.setObject(2, account.getRole());
                ps.setString(3, account.getPassword());
                ps.setInt(4, account.getEmployeeId());

                return ps;
            }, keyHolder);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Returns the keyholder for check
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Failed to obtain generated key for new account");
        }

        account.setId(keyHolder.getKey().intValue());

        return account;
    }
}
