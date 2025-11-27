package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;

import com.mavi.projectmanager.model.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EmployeeRepository employeeRepository;
    private static final Comparator<Account> ACCOUNT_COMPARATOR = Comparator.comparingInt((Account a) -> a.getRole().getId()).thenComparing(Account::getFirstName).thenComparing(Account::getLastName);

    public AccountRepository(JdbcTemplate jdbcTemplate, EmployeeRepository employeeRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.employeeRepository = employeeRepository;
    }

    public final RowMapper<Account> accountRowMapper = ((rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setId(rs.getInt("employee_id"));
        employee.setPosition(rs.getString("position"));
        employee.setMail(rs.getString("mail"));
        employee.setFirstName(rs.getString("firstName"));
        employee.setLastName(rs.getString("lastName"));

        Account account = new Account();
        int roleId = rs.getInt("role");

        account.setId(rs.getInt("id"));
        account.setRole(Role.getRoleByID(roleId));
        account.setPassword(rs.getString("password"));
        account.setEmployee(employee);

        return account;
    });

    //Inserts an account in the database
    public Account createUser(Account account) {

        String query = "INSERT INTO account (role, password, emp_id) VALUES (?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, account.getRole().getId());
                ps.setString(2, account.getPassword());
                ps.setInt(3, account.getEmployee().getId());

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

    public Account getAccountByID(int id){
        String query= """
                        SELECT a.*, e.id AS employee_id, e.position, e.mail, e.firstName, e.lastName FROM Account a
                        INNER JOIN Employee e ON a.emp_id = e.id
                        WHERE a.id = ?
                      """;

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

    public Account getAccountByMail(String mail) {
        String query = """
                        SELECT a.*, e.id AS employee_id, e.position, e.mail, e.firstName, e.lastName FROM Account a
                        INNER JOIN Employee e ON a.emp_id = e.id
                        WHERE e.mail = ?
                      """;

        try{
            return jdbcTemplate.queryForObject(query, accountRowMapper, mail);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
  
    public List<Account> getAccounts() {
        String query = """
                        SELECT a.*, e.id AS employee_id, e.position, e.mail, e.firstName, e.lastName
                        FROM Account a
                        INNER JOIN Employee e ON a.emp_id = e.id
                       """;

        List<Account> accounts = jdbcTemplate.query(query, accountRowMapper);

        accounts.sort(ACCOUNT_COMPARATOR);

        return accounts;
    }
}
