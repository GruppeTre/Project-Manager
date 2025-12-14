package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Comparator;

import java.util.*;
import java.sql.Date;

@Repository
public class ProjectRepository {
    private final JdbcTemplate jdbcTemplate;
    private AccountRepository accountRepository;
    private SubProjectRepository subProjectRepository;
    private static final Comparator<Project> PROJECT_COMPARATOR = Comparator.comparing(Project::getStartDate).thenComparing(Project::getEndDate);

    public ProjectRepository(JdbcTemplate jdbcTemplate, AccountRepository accountRepository, SubProjectRepository subProjectRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountRepository = accountRepository;
        this.subProjectRepository = subProjectRepository;
    }

    //Jens Gotfredsen
    public RowMapper<Project> projectRowMapper = ((rs, rowNum) -> {
        Project project = new Project();
        int projectId = rs.getInt("id");
        project.setId(projectId);
        project.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        project.setStartDate(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        project.setEndDate(convertedEndDate);

        List<Account> projectLeads = accountRepository.getAccountsByProjectId(projectId);

        project.setLeadsList(projectLeads);

        return project;
    });

    //Jens Gotfredsen
    public RowMapper<Project> fullProjectRowMapper = ((rs, rowNum) -> {
        Project project = new Project();
        int projectId = rs.getInt("id");
        project.setId(projectId);

        project.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        project.setStartDate(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        project.setEndDate(convertedEndDate);

        List<SubProject> subProjectsList = subProjectRepository.getSubProjectsByProjectId(projectId);

        project.setSubProjectsList(subProjectsList);

        return project;
    });

    //Jens Gotfredsen
    public List<Project> getProjects() {
        String query = """
                 SELECT p.id, p.name, p.start_date, p.end_date
                 FROM Project p
                 WHERE p.archived = 1
                """;
        List<Project> projects = jdbcTemplate.query(query, projectRowMapper);

        projects.sort(PROJECT_COMPARATOR);

        return projects;
    }

    //Jens Gotfredsen
    public List<Project> getProjectsByLead(int id) {
        String query = """
                        SELECT
                            p.id,
                            p.name,
                            p.start_date,
                            p.end_date
                        FROM Project p
                        INNER JOIN account_project_junction apj
                            ON p.id = apj.project_id
                        WHERE apj.account_id = ? AND p.archived = 1
                """;

        return jdbcTemplate.query(query, projectRowMapper, id);
    }

    //Jens Gotfredsen
    public Project getProjectById(int id) {

        String query = """
                 SELECT p.id, p.name, p.start_date, p.end_date,
                               GROUP_CONCAT(
                                   CONCAT(e.firstName, ' ', e.lastName)
                                   SEPARATOR','
                               ) AS leads
                        FROM Project p
                        LEFT JOIN account_project_junction apj ON p.id = apj.project_id
                        LEFT JOIN Account a ON apj.account_id = a.id
                        LEFT JOIN Employee e ON a.emp_id = e.id
                        WHERE p.id = ?
                        GROUP BY p.id, p.name, p.start_date, p.end_date;
                """;

        return jdbcTemplate.queryForObject(query, projectRowMapper, id);
    }

    //Inserts a project in the database
    //Jacob Klitgaard
    public int createProject(Project project) {

        String query = "INSERT INTO project (name, start_date, end_date, archived) VALUES (?,?,?, 1)";

        int rowsAffected;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, project.getName());
                ps.setObject(2, project.getStartDate());
                ps.setObject(3, project.getEndDate());

                return ps;
            }, keyHolder);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (rowsAffected != 1) {
            throw new RuntimeException("Wrong number of rows inserted. Rows: " + rowsAffected);
        }

        //Returns the keyholder for check
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Failed to obtain generated key for new project");
        }

        project.setId(keyHolder.getKey().intValue());

        return keyHolder.getKey().intValue();
    }

    //Magnus Sørensen
    public Project updateProject(Project project) {

        String query = """
                UPDATE Project
                SET name = ?, start_date = ?, end_date = ?
                WHERE id = ?
                """;

        //returns rows affected
        int rowsAffected = jdbcTemplate.update(query, project.getName(), project.getStartDate(), project.getEndDate(), project.getId());

        if (rowsAffected != 1) {
            throw new RuntimeException("Could not insert into junction table");
        }

        return project;
    }

    public void insertIntoAccountProjectJunction(int accountId, int projectId) {

        int rowsAffected;

        String query = "INSERT INTO account_project_junction (account_id, project_id) VALUES (?, ?)";

        rowsAffected = jdbcTemplate.update(query, accountId, projectId);

        if (rowsAffected != 1) {
            throw new RuntimeException("Could not insert into junction table");
        }
    }

    //Magnus Sørensen
    public void deleteFromAccountProjectJunction(int projectId) {

        String query = """
                DELETE FROM account_project_junction
                WHERE project_id = ?;
                """;

        int rowsAffected = jdbcTemplate.update(query, projectId);

        if (rowsAffected == 0) {
            throw new RuntimeException("unexpected amount of rows deleted! Expected : 1, actual: " + rowsAffected);
        }
    }

    //Jens Gotfredsen
    public Project getFullProjectById(int id) {
        String query = """
                SELECT * FROM Project WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(query, fullProjectRowMapper, id);
    }

    //Emil Gurresø
    public int deleteProject(Project toDelete) {

        String sql = """
                DELETE FROM project
                WHERE id = ?
                """;

        int projectId = toDelete.getId();

        int rowsAffected;

        try {

            rowsAffected = jdbcTemplate.update(sql, projectId);
            //(jdbc template throws DataAccessException)
        } catch (DataAccessException e) {
            throw new RuntimeException("An unexpected error occured while trying to delete project with id: " + projectId);
        }

        return rowsAffected;
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

    public List<Project> getProjectByTeamMember(int id) {
        String query = """
                        SELECT
                            p.id,
                            p.name,
                            p.start_date,
                            p.end_date
                        FROM Project p
                        WHERE EXISTS (
                        SELECT 1
                        FROM Subproject sp
                        JOIN Task t
                            ON t.subproject_id = sp.id
                        JOIN account_task_junction atj
                            ON atj.task_id = t.id
                        WHERE sp.project_id = p.id
                            AND atj.account_id = ?
                            AND p.archived = 1
                        )
                """;

        return jdbcTemplate.query(query, projectRowMapper, id);
    }

    public int archiveProject(Project project){
        String query = """
                    UPDATE Project
                    SET archived = 0
                    WHERE id = ?
                """;

        int rowsAffected;
        try {
            rowsAffected = jdbcTemplate.update(query, project.getId());
        } catch (DataAccessException e) {
            throw new RuntimeException("An unexpected error occurred when attempting to archive project with id: " + project.getId());
        }

        return rowsAffected;
    }

    public List<Project> getArchivedProjects(){
        String query = """
             SELECT p.id, p.name, p.start_date, p.end_date
             FROM Project p
             WHERE p.archived = 0
            """;
        List<Project> projects = jdbcTemplate.query(query, projectRowMapper);

        projects.sort(PROJECT_COMPARATOR);

        return projects;
    }
}
