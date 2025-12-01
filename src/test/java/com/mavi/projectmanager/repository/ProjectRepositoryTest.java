package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldGetProjects()     {
        List<Project> projectsList = projectRepository.getProjects();

        assertFalse(projectsList.isEmpty());
        assertEquals(1, projectsList.getFirst().getId());
        assertEquals("Projekt Alpha", projectsList.getFirst().getName());
        assertEquals(LocalDate.of(2025, 11, 28), projectsList.getFirst().getStart_date());
        assertEquals(LocalDate.of(2025, 11, 30), projectsList.getFirst().getEnd_date());

    }
}