package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Comparator;

import java.time.LocalDate;
import java.util.*;
import java.sql.Date;

@Repository
public class ProjectRepository {
    private final JdbcTemplate jdbcTemplate;
    private AccountRepository accountRepository;
    private static final Comparator<Project> PROJECT_COMPARATOR = Comparator.comparing(Project::getStart_date).thenComparing(Project::getEnd_date);

    public ProjectRepository(JdbcTemplate jdbcTemplate, AccountRepository accountRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.accountRepository = accountRepository;
    }

    public RowMapper<Project> projectRowMapper = ((rs, rowNum) -> {
        Project project = new Project();
        int projectId = rs.getInt("id");
        project.setId(projectId);
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

    //Inserts a project in the database
    public int createProject(Project project, Employee employee) {

        String query = "INSERT INTO project (name, start_date, end_date) VALUES (?,?,?)";

        Account account = accountRepository.getAccountByMail(employee.getMail());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, project.getName());
                ps.setObject(2, project.getStart_date());
                ps.setObject(3, project.getEnd_date());

                return ps;
            }, keyHolder);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Returns the keyholder for check
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Failed to obtain generated key for new project");
        }

        project.setId(keyHolder.getKey().intValue());

        return keyHolder.getKey().intValue();
    }

    public int updateAccountProjectJunction(int accountId, int projectId) {

        int rowsAffected;

        String query = "INSERT INTO account_project_junction (account_id, project_id) VALUES (?, ?)";

        rowsAffected = jdbcTemplate.update(query, accountId, projectId);

        if(rowsAffected != 1) {
            throw new RuntimeException("Could not insert into junction table");
        }

       return rowsAffected;

    }
}
