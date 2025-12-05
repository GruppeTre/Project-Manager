package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    private Project dbProjectWithLead;
    private Project dbProjectWithoutLead;
    private Account dbProjectLead;
    private Project dbProjectToInsert;

    @BeforeEach
    void setUp() {
        Employee dbEmployee = new Employee();
        dbEmployee.setPosition("Udvikler");
        dbEmployee.setFirstName("Ida");
        dbEmployee.setLastName("S�rensen");
        dbEmployee.setMail("idso@alphasolutions.com");
        dbEmployee.setId(2);

        dbProjectLead = new Account();
        dbProjectLead.setId(2);
        dbProjectLead.setRole(Role.PROJECT_LEAD);
        dbProjectLead.setEmployee(dbEmployee);

        dbProjectWithLead = new Project();
        dbProjectWithLead.setName("Projekt Alpha");
        dbProjectWithLead.setStart_date(LocalDate.parse("2025-11-28"));
        dbProjectWithLead.setEnd_date(LocalDate.parse("2025-11-30"));
        dbProjectWithLead.setId(1);

        dbProjectWithoutLead = new Project();
        dbProjectWithoutLead.setName("Projekt Beta");
        dbProjectWithoutLead.setStart_date(LocalDate.parse("2025-12-01"));
        dbProjectWithoutLead.setEnd_date(LocalDate.parse("2025-12-17"));
        dbProjectWithoutLead.setId(2);

        dbProjectToInsert = new Project();
        dbProjectToInsert.setName("Project Gamma");
        dbProjectToInsert.setStart_date(LocalDate.parse("2025-11-28"));
        dbProjectToInsert.setEnd_date(LocalDate.parse("2025-11-30"));

    }

    @Test
    void shouldGetProjects() {
        List<Project> projectsList = projectRepository.getProjects();

        assertFalse(projectsList.isEmpty());
        assertEquals(1, projectsList.getFirst().getId());
        assertEquals("Projekt Alpha", projectsList.getFirst().getName());
        assertEquals(LocalDate.of(2025, 11, 28), projectsList.getFirst().getStart_date());
        assertEquals(LocalDate.of(2025, 11, 30), projectsList.getFirst().getEnd_date());
    }

    @Test
    void shouldUpdateValidProject() {

        String newName = "New Name";

        LocalDate oldStartDate = dbProjectWithLead.getStart_date();
        LocalDate oldEndDate = dbProjectWithLead.getEnd_date();

        dbProjectWithLead.setName(newName);

        Project updatedProject = this.projectRepository.updateProject(dbProjectWithLead);

        assertEquals(newName, updatedProject.getName());
        assertEquals(oldStartDate, updatedProject.getStart_date());
        assertEquals(oldEndDate, updatedProject.getEnd_date());
    }

    @Test
    void shouldInsertIntoAccountProjectJunction() {
        assertDoesNotThrow(() -> this.projectRepository.insertIntoAccountProjectJunction(dbProjectLead.getId(), dbProjectWithoutLead.getId()));

        assertEquals(this.projectRepository.getProjectsByLead(dbProjectLead.getId()), List.of(dbProjectWithLead, dbProjectWithoutLead));
    }

    @Test
    void shouldInsertIntoProject() {

        int expectedId = 3;

        assertDoesNotThrow(() -> this.projectRepository.createProject(dbProjectToInsert));

        dbProjectToInsert.setId(expectedId);

        assertEquals(this.projectRepository.getProjectById(expectedId), dbProjectToInsert);
    }
    @Test
    void shouldDeleteProject() {
        assertEquals(1, this.projectRepository.deleteProject(dbProjectWithLead));
    }

}