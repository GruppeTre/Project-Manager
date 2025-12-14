package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Task;
import org.springframework.dao.DataAccessException;
import com.mavi.projectmanager.model.SubProject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Repository
public class SubProjectRepository {

    private final JdbcTemplate jdbcTemplate;
    private TaskRepository taskRepository;
    private static final Comparator<SubProject> SUB_PROJECT_COMPARATOR = Comparator.comparing(SubProject::getStartDate).thenComparing(SubProject::getEndDate);

    public SubProjectRepository(JdbcTemplate jdbcTemplate, TaskRepository taskRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRepository = taskRepository;
    }

    public RowMapper<SubProject> subProjectRowMapper = ((rs, rowNum) -> {
        SubProject subProject = new SubProject();
        int subProjectId = rs.getInt("id");
        subProject.setId(subProjectId);
        subProject.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        subProject.setStartDate(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        subProject.setEndDate(convertedEndDate);

        return subProject;
    });

    //Jens Gotfredsen
    public RowMapper<SubProject> subProjectRowMapperForFullProject = ((rs, rowNum) -> {
        SubProject subProject = new SubProject();

        int subProjectId = rs.getInt("id");
        subProject.setId(subProjectId);
        subProject.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        subProject.setStartDate(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        subProject.setEndDate(convertedEndDate);

        List<Task> taskList = taskRepository.getTaskBySubProjectId(subProjectId);
        subProject.setTaskList(taskList);

        return subProject;
    });

    public SubProject getSubProjectById(int id) {
        String query = """
                SELECT * FROM Subproject sp WHERE id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(query, subProjectRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //Inserts a subproject in the database
    public int createSubProject(SubProject subProject, int projectId) {

        String query = "INSERT INTO subproject (name, start_date, end_date, project_id) VALUES (?,?,?,?)";

        int rowsAffected;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, subProject.getName());
                ps.setObject(2, subProject.getStartDate());
                ps.setObject(3, subProject.getEndDate());
                ps.setInt(4, projectId);

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

        subProject.setId(keyHolder.getKey().intValue());

        return keyHolder.getKey().intValue();
    }

    public int deleteSubProject(SubProject toDelete) {

        String sql = """
                DELETE FROM subproject
                WHERE id = ?
                """;

        int rowsAffected;

        try {
            rowsAffected = jdbcTemplate.update(sql, toDelete.getId());
        } catch (DataAccessException e) {
            throw new RuntimeException("An unexpected error occured while trying to delete subproject with id: " + toDelete.getId());
        }

        return rowsAffected;
    }

    public SubProject getSubprojectById(int id) {

        String sql = """
                SELECT s.id, s.name, s.start_date, s.end_date 
                FROM subproject s 
                WHERE s.id = ?
                """;

        return jdbcTemplate.queryForObject(sql, subProjectRowMapper, id);

    }

    //returns rowsAffected - catches DataAccessException - checks for?
    //Checks for consistency in end_date and start_date between these fields in SubProejct, Task and Project.
    //ToDo: the above mentioned.
    public int updateSubProject(SubProject subProject) {

        String sql = """
                UPDATE subproject
                SET name = ?, start_date = ?, end_date = ?
                WHERE id = ?""";

        String name = subProject.getName();
        LocalDate startDate = subProject.getStartDate();
        LocalDate endDate = subProject.getEndDate();
        int id = subProject.getId();

        int rowsAffected = jdbcTemplate.update(sql, name, startDate, endDate, id);

        if(rowsAffected != 1) {
            throw new RuntimeException("Could not update the Subproject");
        }

        return rowsAffected;
    }

    //Jens Gotfredsen
    public List<SubProject> getSubProjectsByProjectId(int id) {
        String query = """
                SELECT * FROM Subproject WHERE project_id = ?
                """;

        return jdbcTemplate.query(query, subProjectRowMapperForFullProject, id);
    }

}