package com.mavi.projectmanager.repository;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.model.Task;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;
    private AccountRepository accountRepository;

    public TaskRepository(JdbcTemplate jdbcTemplate, AccountRepository accountRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.accountRepository = accountRepository;
    }

    //Jens Gotfredsen
    public final RowMapper<Task> taskRowMapper = ((rs, rowNum) ->{
        Task task = new Task();

        task.setId(rs.getInt("id"));
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        task.setStartDate(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        task.setEndDate(convertedEndDate);

        task.setEstimatedDuration(rs.getInt("estimated_duration"));

        task.setActualDuration(rs.getInt("actual_duration"));

        task.setArchived(rs.getInt("archived"));

        return task;
    });

    //Jens Gotfredsen
    public final RowMapper<Task> taskRowMapperForFullProject = ((rs, rowNum) -> {
        Task task = new Task();

        int taskId = rs.getInt("id");
        task.setId(taskId);
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        task.setStartDate(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        task.setEndDate(convertedEndDate);

        task.setEstimatedDuration(rs.getInt("estimated_duration"));

        task.setActualDuration(rs.getInt("actual_duration"));

        task.setArchived(rs.getInt("archived"));

        List<Account> accountList = accountRepository.getAccountsByTaskId(taskId);
        task.setAccountList(accountList);

        return task;
    });

    //Jens Gotfredsen
    public Task createTask(Task task, SubProject subProject) {

        String query = """
                INSERT INTO task (
                name,
                description,
                start_date,
                end_date,
                estimated_duration,
                archived,
                subproject_id
                )
                VALUES (?,?,?,?,?, 1, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, task.getName());
                ps.setString(2, task.getDescription());
                ps.setDate(3, java.sql.Date.valueOf(task.getStartDate()));
                ps.setObject(4, java.sql.Date.valueOf(task.getEndDate()));
                ps.setInt(5, task.getEstimatedDuration());
                ps.setInt(6, subProject.getId());

                return ps;
            }, keyHolder);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Returns the keyholder for check
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Failed to obtain generated key for new account");
        }

        task.setId(keyHolder.getKey().intValue());

        return task;
    }

    //Magnus Sørensen
    public void updateTask(Task task) {

        String sql = """
                UPDATE task
                SET name = ?, description = ?, start_date = ?, end_date = ?, estimated_duration = ?
                WHERE id = ?
                """;

        int rowsAffected = jdbcTemplate.update(sql, task.getName(),
                task.getDescription(),
                task.getStartDate(),
                task.getEndDate(),
                task.getEstimatedDuration(),
                task.getId());

        if (rowsAffected != 1) {
            throw new RuntimeException("Failed to update project! Expected rows changed: 1, actual: " + rowsAffected);
        }
    }

    //Jens Gotfredsen
    public void addEmployeesToTaskJunction(Task task) {

        String query = """
                INSERT INTO account_task_junction (
                task_id,
                account_id
                )
                VALUES (?, ?)
                """;

        List<Integer> accountIds = new ArrayList<>();

        for(Account account : task.getAccountList()){
            accountIds.add(account.getId());
        }

        jdbcTemplate.batchUpdate(query, accountIds, accountIds.size(), (ps, accountId) -> {
            ps.setInt(1, task.getId());
            ps.setInt(2, accountId);
        });
    }

    //Magnus Sørensen
    public void deleteFromEmployeesToTaskJunction(int taskId) {

        String sql = """
                DELETE FROM account_task_junction
                WHERE task_id = ?
                """;

        jdbcTemplate.update(sql, taskId);
    }

    //Jens Gotfredsen
    public Task getTaskById(int id) {

        String query = """
                SELECT *
                FROM task
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(query, taskRowMapper, id);
    }

    public int archiveTask(Task task) {

        String query = """
                    UPDATE task
                    SET archived = 0, actual_duration = ?
                    WHERE id = ?
                """;

        int rowsAffected;

        try {
            rowsAffected = jdbcTemplate.update(query, task.getActualDuration(), task.getId());
        } catch (DataAccessException e) {
            throw new RuntimeException("An unexpected error occurred when attempting to archive project with id: " + task.getId());
        }

        return rowsAffected;
    }

    //Jens Gotfredsen
    public List<Task> getTaskBySubProjectId(int id) {
        String query = """
                SELECT * FROM task WHERE subproject_id = ?
                """;

        return jdbcTemplate.query(query, taskRowMapperForFullProject, id);
    }

    //Magnus Sørensen
    public int deleteTask(Task toDelete) {

        String sql = """
                DELETE FROM task
                WHERE id = ?
                """;

        int taskId = toDelete.getId();

        int rowsAffected;

        try {
            rowsAffected = jdbcTemplate.update(sql, taskId);
        } catch (DataAccessException e) {
            throw new RuntimeException("An unexpected error occured while trying to delete task with id: " + taskId);
        }

        return rowsAffected;
    }
}
