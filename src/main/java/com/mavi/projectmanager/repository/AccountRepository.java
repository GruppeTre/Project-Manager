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
    private final JdbcTemplate jdbcTemplate;
    private EmployeeRepository employeeRepository;

    public AccountRepository(JdbcTemplate jdbcTemplate, EmployeeRepository employeeRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.employeeRepository = employeeRepository;
    }

    public final RowMapper<Account> accountRowMapper = ((rs, rowNum) -> {
        Account account = new Account();
        int roleId = rs.getInt("role");

        account.setId(rs.getInt("id"));
        account.setRole(Role.getRoleByID(roleId));
        account.setPassword(rs.getString("password"));
        account.setEmployee(employeeRepository.getEmployeeByID(rs.getInt("emp_id")));

        return account;
    });

    public Account getAccountByID(int id){
        String query= "SELECT * FROM Account WHERE id = ?";

        try{
            return jdbcTemplate.queryForObject(query, accountRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Account updatedAccount(Account updatedAccount){
        String query = "UPDATE Account SET role = ?, password = ? WHERE id = ?";

        int accountID = updatedAccount.getId();
        int role = updatedAccount.getRole().getId();
        String password = updatedAccount.getPassword();

        int rowsAffected = jdbcTemplate.update(query, role, password, accountID);

        if (rowsAffected > 1) {
            throw new RuntimeException("Multiple users with id: " + accountID);
        }
        if (rowsAffected == 0) {
            return null;
        }

        return updatedAccount;


    }
}
