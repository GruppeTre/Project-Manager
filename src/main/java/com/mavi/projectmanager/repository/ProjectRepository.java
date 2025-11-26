package com.mavi.projectmanager.repository;


import com.mavi.projectmanager.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Project> getProjects(){
        String query = """
                        SELECT p.id AS project_id, p.name, p.start_date AS p_start, p.end_date AS p_end,
                        apj.account_id AS apj_account_id, apj.project_id AS apj_project_id,
                        a.id AS account_id, a.role, a.password, a.emp_id
                        FROM Projects p
                        INNER JOIN account_project_junction apj ON Projects p.id = apj.project_id
                        INNER JOIN Account a ON account_project_junction ON Account a.account_id = apj.account_id
                       """;

    }
}
