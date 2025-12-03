package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.repository.AccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
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
}
