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
    private static final Comparator<Project> PROJECT_COMPARATOR = Comparator.comparing(Project::getStart_date).thenComparing(Project::getEnd_date);

    public ProjectRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
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

        String projectLeads = rs.getString("leads");

        if(projectLeads != null){
            List<String> projectLeadsList = Arrays.asList(projectLeads.split(","));
            project.setLeadsList(projectLeadsList);

        }
        else {
            project.setLeadsList(Collections.emptyList());
        }

        return project;
    });

    public List<Project> getProjects(){
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
                        GROUP BY p.id, p.name, p.start_date, p.end_date;
                       """;
        List<Project> projects = jdbcTemplate.query(query, projectRowMapper);

        projects.sort(PROJECT_COMPARATOR);

        return projects;
    }

    //Inserts a project in the database
    public Project createProject(Project project, Account account) {

        String query = "INSERT INTO project (name, start_date, end_date) VALUES (?,?,?)";

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

        return project;
    }

}
