package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.SubProject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;

@Repository
public class SubProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubProjectRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public RowMapper<SubProject> subProjectRowMapper = ((rs, rowNum) -> {
        SubProject subProject = new SubProject();

        subProject.setId(rs.getInt("id"));
        subProject.setName(rs.getString("name"));

        Date startDate = rs.getDate("start_date");
        LocalDate convertedStartDate = startDate.toLocalDate();
        subProject.setStart_date(convertedStartDate);

        Date endDate = rs.getDate("end_date");
        LocalDate convertedEndDate = endDate.toLocalDate();
        subProject.setEnd_date(convertedEndDate);

        return subProject;
    });

    public SubProject getSubProjectById(int id){
        String query = """
                SELECT * FROM Subproject sp WHERE id = ?
                """;

        try{
            return jdbcTemplate.queryForObject(query, subProjectRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
