package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public final RowMapper<Employee> employeeRowMapper = ((rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setPosition(rs.getString("position"));
        employee.setMail(rs.getString("mail"));
        employee.setFirstName(rs.getString("firstName"));
        employee.setLastName(rs.getString("lastName"));

        return employee;
    });
  
    public Employee getEmployeeByMail(String mail) {

        String query = "SELECT * FROM Employee WHERE mail = ?";

        try {
            return jdbcTemplate.queryForObject(query, employeeRowMapper, mail);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
