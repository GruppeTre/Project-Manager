package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
}
