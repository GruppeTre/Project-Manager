package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
    private JdbcTemplate jdbcTemplate;

    public final RowMapper<Account> accountRowMapper = ((rs, rowNum) -> {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setRole(Role.values()[rs.getInt("role")]);
        account.setPassword(rs.getString("password"));
        account.setEmployee(getEmployeeByID());

        return account;
    });


    public Employee getEmployeeByID(int id){
        String query = "SELECT * FROM Employee WHERE id = ?";

        try{
            return jdbcTemplate.queryForObject(query, employeeRowMapper, id);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    public Account getAccountByID(int id){
        String query= "SELECT * FROM Account WHERE id = ?";

        try{
            return jdbcTemplate.queryForObject(query, accountRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
