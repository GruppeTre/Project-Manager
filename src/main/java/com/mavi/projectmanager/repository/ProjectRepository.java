package com.mavi.projectmanager.repository;


import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Project;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.sql.Date;
import java.util.List;

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

        return project;
    });

    public List<Project> getProjects(){
        String query = """
                        SELECT * FROM Project;
                       """;
        List<Project> projects = jdbcTemplate.query(query, projectRowMapper);

        projects.sort(PROJECT_COMPARATOR);

        return projects;
    }

    public int deleteProjectByProject(Project toDelete) {

        String sql = """
                DELETE FROM project WHERE id = ?""";

        int projectID = toDelete.getId();

        try {

            int rowsAffected = jdbcTemplate.update(sql, projectID);

            //Signal, on whether the database is corrupt.
            if (rowsAffected > 1) {
                throw new RuntimeException("Multiple lists was found with this id: " + projectID + ", and it is unclear what Project to delete. Please contact dataspecialist");
            }
            if (rowsAffected == 0) {
                return 0;
            }

            return rowsAffected;
            //(jdbc template throws DataAccessException)
        } catch (DataAccessException e) {
            throw new RuntimeException("A database error occurred when trying to delete project with ID: " + projectID + ". Please contact data specialist.");
        }
    }


}
