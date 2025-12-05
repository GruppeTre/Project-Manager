package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import com.mavi.projectmanager.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Comparator;

import java.time.LocalDate;
import java.util.*;
import java.sql.Date;
import java.util.List;

@Repository
public class ProjectRepository {
    private final JdbcTemplate jdbcTemplate;
    private EmployeeRepository employeeRepository;
    private AccountRepository accountRepository;
    private static final Comparator<Project> PROJECT_COMPARATOR = Comparator.comparing(Project::getStart_date).thenComparing(Project::getEnd_date);
    private final AccountRepository accountRepository;

    public ProjectRepository(JdbcTemplate jdbcTemplate, AccountRepository accountRepository, EmployeeRepository employeeRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
    }

    public RowMapper<Project> projectRowMapper = ((rs, rowNum) -> {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        project.setStart_date(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        project.setEnd_date(convertedEndDate);

        List<Account> projectLeads = accountRepository.getAccountsByProjectId(projectId);

        project.setLeadsList(projectLeads);

        return project;
    });
    //RowMapper with only with fields: ID, Name, Start_Date, End_Date.
    public RowMapper<Project> projectExclusiveRowMapper = ((rs, rowNum) -> {
        Project projectExclusive = new Project();
        projectExclusive.setId(rs.getInt("id"));
        projectExclusive.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();

        return projectExclusive;
    });



    public RowMapper<Project> fullProjectRowMapper = ((rs, rowNum) -> {
        Project project = new Project();
        int projectId = rs.getInt(rs.getInt("id"));
        project.setId(projectId);

        project.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        project.setStart_date(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        project.setEnd_date(convertedEndDate);

        List<SubProject> subProjectsList = getSubProjectsByProjectId(projectId);

        project.setSubProjectsList(subProjectsList);

        return project;
    });

    public RowMapper<SubProject> subProjectRowMapper = ((rs, rowNum) -> {
        SubProject subProject = new SubProject();

        int subProjectId = rs.getInt("id");
        subProject.setId(subProjectId);
        subProject.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        subProject.setStart_date(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        subProject.setEnd_date(convertedEndDate);

        List<Task> taskList = getTaskBySubProjectId(subProjectId);
        subProject.setTaskList(taskList);

        return subProject;
    });

    public RowMapper<Task> taskRowMapper = ((rs, rowNum) -> {
        Task task = new Task();

        int taskId = rs.getInt("id");
        task.setId(taskId);
        task.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        task.setStart_date(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        task.setEnd_date(convertedEndDate);

        task.setEstimatedDuration(rs.getInt("duration"));

        List<Employee> employeeList = employeeRepository.getEmployeeByTaskId(taskId);
        task.setEmployeeList(employeeList);

        return task;
    });

    public List<Project> getProjects(){
        String query = """
                        SELECT p.id, p.name, p.start_date, p.end_date
                        FROM Project p
                       """;
        List<Project> projects = jdbcTemplate.query(query, projectRowMapper);

        projects.sort(PROJECT_COMPARATOR);

        return projects;
    }

    public List<Project> getProjectsByLead(int id){
        String query = """
                        SELECT
                            p.id,
                            p.name,
                            p.start_date,
                            p.end_date
                        FROM Project p
                        INNER JOIN account_project_junction apj
                            ON p.id = apj.project_id
                        WHERE apj.account_id = ?
                """;

        return jdbcTemplate.query(query, projectRowMapper, id);
    }

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
    public int createProject(Project project) {

        String query = "INSERT INTO project (name, start_date, end_date) VALUES (?,?,?)";

        int rowsAffected;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, project.getName());
                ps.setObject(2, project.getStart_date());
                ps.setObject(3, project.getEnd_date());

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

    // Todo: Should probably be return rowsAffected
    public Project updateProject(Project project) {

        String query = """
                UPDATE Project
                SET name = ?, start_date = ?, end_date = ?
                WHERE id = ?
                """;

        //returns rows affected
        int rowsAffected = jdbcTemplate.update(query, project.getName(), project.getStart_date(), project.getEnd_date(), project.getId());

        if(rowsAffected != 1) {
            throw new RuntimeException("Could not insert into junction table");
        }

        return project;
    }

    public void insertIntoAccountProjectJunction(int accountId, int projectId) {

        int rowsAffected;

        String query = "INSERT INTO account_project_junction (account_id, project_id) VALUES (?, ?)";

        rowsAffected = jdbcTemplate.update(query, accountId, projectId);

        if(rowsAffected != 1) {
            throw new RuntimeException("Could not insert into junction table");
        }

       return rowsAffected;

    }

    public void deleteFromAccountProjectJunction(int projectId) {

        String query = """
                DELETE FROM account_project_junction
                WHERE project_id = ?;
                """;

        int rowsAffected = jdbcTemplate.update(query, projectId);

        if (rowsAffected != 1) {
            throw new RuntimeException("unexpected amount of rows deleted! Expected : 1, actual: " + rowsAffected);
        }
    }

    public Project getFullProjectById(int id){
        String query = """
                SELECT * FROM Project WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(query, fullProjectRowMapper, id);
    }

    public List<SubProject> getSubProjectsByProjectId(int id){
        String query = """
                SELECT * FROM Subproject WHERE project_id = ?
                """;

        return jdbcTemplate.query(query, subProjectRowMapper, id);
    }

    public List<Task> getTaskBySubProjectId(int id){
        String query = """
                SELECT * FROM Task WHERE subproject_id = ?
                """;

        return jdbcTemplate.query(query, taskRowMapper, id);
    }
    //Helper method:
    //Retrieves a project, but only with fields: name, start_date and end_date - no arraylist or nested object.
    public Project getProjectById(int id) {
        String sql = """
                SELECT p.id p.name, p.start_date, p.end_date FROM project WHERE p.id = ?
                """;
        try {
            return jdbcTemplate.queryForObject(sql, projectExclusiveRowMapper, id);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
