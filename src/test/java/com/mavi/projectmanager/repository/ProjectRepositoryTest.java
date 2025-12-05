package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import net.bytebuddy.asm.Advice;
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

    private Project dbProjectWithLead;
    private Project dbProjectWithoutLead;
    private Account dbProjectLead;

    @BeforeEach
    void setUp() {
        Employee dbEmployee = new Employee();
        dbEmployee.setPosition("Udvikler");
        dbEmployee.setFirstName("Ida");
        dbEmployee.setLastName("Sørensen");
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

    /*
INSERT INTO project(name, start_date, end_date)
VALUES ('Projekt Alpha', '2025-11-28', '2025-11-30'),
       ('Projekt Beta', '2025-12-01', '2025-12-17');

INSERT INTO account_project_junction(account_id, project_id)
VALUES (2,1);

INSERT INTO subproject(name, start_date, end_date, project_id)
VALUES
('Subproject Charlie', '2025-12-12', '2025-12-18', 1),
('Subproject Delta', '2025-12-18', '2025-12-20', 1);

INSERT INTO task(name, description, start_date, end_date, estimated_duration, subproject_id)
VALUES
('Task A', 'Test beskrivelse', '2025-12-12', '2025-12-13', 8, 1),
('Task B', 'Test beskrivelse', '2025-12-13', '2025-12-15', 24, 1);

INSERT INTO employee_task_junction(employee_id, task_id)
VALUES
(1, 1),
(2, 2);
     */

    @Test
    void shouldGetFullProjectById() {
        Project project = projectRepository.getFullProjectById(1);

        assertEquals(1, project.getId());
        assertEquals("Projekt Alpha", project.getName());
        assertEquals(LocalDate.parse("2025-11-28"), project.getStart_date());
        assertEquals(LocalDate.parse("2025-11-30"), project.getEnd_date());
        assertFalse(project.getSubProjectsList().isEmpty());
        assertEquals(1, project.getSubProjectsList().getFirst().getId());
        assertEquals("Subproject Charlie", project.getSubProjectsList().getFirst().getName());
        assertEquals(LocalDate.parse("2025-12-12"), project.getSubProjectsList().getFirst().getStart_date());
        assertEquals(LocalDate.parse("2025-12-18"), project.getSubProjectsList().getFirst().getEnd_date());
        assertFalse(project.getSubProjectsList().isEmpty());
        assertEquals(1, project.getSubProjectsList().getFirst().getTaskList().getFirst().getId());
        assertEquals("Task A", project.getSubProjectsList().getFirst().getTaskList().getFirst().getName());
        assertEquals("Test beskrivelse", project.getSubProjectsList().getFirst().getTaskList().getFirst().getDescription());
        assertEquals(LocalDate.parse("2025-12-12"), project.getSubProjectsList().getFirst().getTaskList().getFirst().getStart_date());
        assertEquals(LocalDate.parse("2025-12-13"), project.getSubProjectsList().getFirst().getTaskList().getFirst().getEnd_date());
        assertEquals(8, project.getSubProjectsList().getFirst().getTaskList().getFirst().getEstimatedDuration());
        assertFalse(project.getSubProjectsList().getFirst().getTaskList().getFirst().getEmployeeList().isEmpty());
        assertEquals(1, project.getSubProjectsList().getFirst().getTaskList().getFirst().getEmployeeList().getFirst().getId());

    }

}