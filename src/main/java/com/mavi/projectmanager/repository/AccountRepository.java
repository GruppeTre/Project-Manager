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
    public Account createAccount(Account account) {

        String query = "INSERT IGNORE INTO account (id, role, password, employee) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, account.getId());
                ps.setObject(1, account.getRole());
                ps.setString(2, account.getPassword());
                ps.setString(3, account.getFirstName());
                ps.setString(4, account.getLastName());
                ps.setString(4, account.getMail());

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
