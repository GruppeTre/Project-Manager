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

    public AccountRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public final RowMapper<Account> accountRowMapper = ((rs, rowNum) -> {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setRole(Role.values()[rs.getInt("role")]);
        account.setPassword(rs.getString("password"));
        account.setEmployee(getEmployeeByID(rs.getInt("emp_id")));

        return account;
    });

    public final RowMapper<Employee> employeeRowMapper = ((rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setPosition(rs.getString("position"));
        employee.setMail(rs.getString("mail"));
        employee.setFirstName("firstName");
        employee.setLastName("lastName");

        return employee;
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
