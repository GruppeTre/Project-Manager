package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.*;
import org.junit.jupiter.api.BeforeEach;
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
    @Autowired
    private TaskRepository taskRepository;

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
        dbProjectWithLead.setStartDate(LocalDate.parse("2025-11-28"));
        dbProjectWithLead.setEndDate(LocalDate.parse("2025-11-30"));
        dbProjectWithLead.setId(1);

        Task dbTask = new Task();
        dbTask.setId(1);
        dbTask.setName("task A");
        dbTask.setDescription("test beskrivelse");
        dbTask.setStartDate(LocalDate.parse("2025-12-12"));
        dbTask.setEndDate(LocalDate.parse("2025-12-13"));
        dbTask.setEstimatedDuration(8);

        List<Task> taskList = List.of(dbTask);

        SubProject dbSubProject = new SubProject();
        dbSubProject.setId(1);
        dbSubProject.setName("Subproject Charlie");
        dbSubProject.setStartDate(LocalDate.parse("2025-12-12"));
        dbSubProject.setStartDate(LocalDate.parse("2025-12-18"));
        dbSubProject.setTaskList(taskList);

        List<SubProject> subProjectList = List.of(dbSubProject);

        dbProjectWithLead.setSubProjectsList(subProjectList);

        dbProjectWithoutLead = new Project();
        dbProjectWithoutLead.setName("Projekt Beta");
        dbProjectWithoutLead.setStartDate(LocalDate.parse("2025-12-01"));
        dbProjectWithoutLead.setEndDate(LocalDate.parse("2025-12-17"));
        dbProjectWithoutLead.setId(2);

        dbProjectToInsert = new Project();
        dbProjectToInsert.setName("Project Charlie");
        dbProjectToInsert.setStartDate(LocalDate.parse("2025-11-28"));
        dbProjectToInsert.setEndDate(LocalDate.parse("2025-11-30"));

    }

    @Test
    void shouldGetProjects() {
        List<Project> projectsList = projectRepository.getProjects();

        assertFalse(projectsList.isEmpty());
        assertEquals(1, projectsList.getFirst().getId());
        assertEquals("Projekt Alpha", projectsList.getFirst().getName());
        assertEquals(LocalDate.of(2025, 11, 28), projectsList.getFirst().getStartDate());
        assertEquals(LocalDate.of(2025, 11, 30), projectsList.getFirst().getEndDate());
    }

    @Test
    void shouldUpdateValidProject() {

        String newName = "New Name";

        LocalDate oldStartDate = dbProjectWithLead.getStartDate();
        LocalDate oldEndDate = dbProjectWithLead.getEndDate();

        dbProjectWithLead.setName(newName);

        Project updatedProject = this.projectRepository.updateProject(dbProjectWithLead);

        assertEquals(newName, updatedProject.getName());
        assertEquals(oldStartDate, updatedProject.getStartDate());
        assertEquals(oldEndDate, updatedProject.getEndDate());
    }

    @Test
    void shouldInsertIntoAccountProjectJunction() {
        assertDoesNotThrow(() -> this.projectRepository.insertIntoAccountProjectJunction(dbProjectLead.getId(), dbProjectWithoutLead.getId()));

        assertEquals(this.projectRepository.getProjectsByLead(dbProjectLead.getId()), List.of(dbProjectWithLead, dbProjectWithoutLead));
    }

    @Test
    void shouldGetFullProjectById() {
        Project project = projectRepository.getFullProjectById(1);

        assertEquals(1, project.getId());
        assertEquals("Projekt Alpha", project.getName());
        assertEquals(LocalDate.parse("2025-11-28"), project.getStartDate());
        assertEquals(LocalDate.parse("2025-11-30"), project.getEndDate());

        List<SubProject> sp = project.getSubProjectsList();
        assertFalse(sp.isEmpty());
        assertEquals(1, sp.getFirst().getId());
        assertEquals("Subproject Charlie", sp.getFirst().getName());
        assertEquals(LocalDate.parse("2025-12-12"), sp.getFirst().getStartDate());
        assertEquals(LocalDate.parse("2025-12-18"), sp.getFirst().getEndDate());

        List<Task> t = sp.getFirst().getTaskList();
        assertFalse(t.isEmpty());
        assertEquals(1, t.getFirst().getId());
        assertEquals("Task A", t.getFirst().getName());
        assertEquals("Test beskrivelse", t.getFirst().getDescription());
        assertEquals(LocalDate.parse("2025-12-12"), t.getFirst().getStartDate());
        assertEquals(LocalDate.parse("2025-12-13"), t.getFirst().getEndDate());
        assertEquals(8, t.getFirst().getEstimatedDuration());
        assertFalse(project.getSubProjectsList().getFirst().getTaskList().getFirst().getAccountList().isEmpty());
        assertEquals(1, project.getSubProjectsList().getFirst().getTaskList().getFirst().getAccountList().getFirst().getId());

    }

    //Jacob Klitgaard
    @Test
    void shouldInsertIntoProject() {

        int expectedId = 3;

        assertDoesNotThrow(() -> this.projectRepository.createProject(dbProjectToInsert));

        dbProjectToInsert.setId(expectedId);

        assertEquals(this.projectRepository.getProjectById(expectedId), dbProjectToInsert);
    }

    //Emil Gurresø
    @Test
    void shouldDeleteProject() {
        assertEquals(1, this.projectRepository.deleteProject(dbProjectWithLead));
    }

    //Magnus Sørensen
    @Test
    void shouldDeleteTask() {

        //retrieve first task of first subproject
        Task toDelete = dbProjectWithLead.getSubProjectsList().getFirst().getTaskList().getFirst();

        assertEquals(1, this.taskRepository.deleteTask(toDelete));
    }
}