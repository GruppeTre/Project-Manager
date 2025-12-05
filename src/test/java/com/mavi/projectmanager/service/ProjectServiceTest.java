package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProjectServiceTest {

    @Autowired
    ProjectService projectService;

    @MockitoBean
    ProjectRepository projectRepository;

    @MockitoBean
    AccountRepository accountRepository;

    private Project dbProject;
    private Project newProject;
    private Account leadAccount;

    @BeforeEach
    void setUp() {
        String mail = "ProjectLead@alphasolutions.com";
        String firstName = "Erik";
        String lastName = "Eriksen";
        String position = "Lead";
        Role role = Role.PROJECT_LEAD;
        int accountId = 1;

        Employee dbEmployee = new Employee();
        dbEmployee.setMail(mail);
        dbEmployee.setPosition(position);
        dbEmployee.setFirstName(firstName);
        dbEmployee.setLastName(lastName);
        dbEmployee.setId(accountId);

        leadAccount = new Account();
        leadAccount.setEmployee(dbEmployee);
        leadAccount.setRole(role);
        leadAccount.setId(accountId);

        List<Account> defaultLeadsList = List.of(leadAccount);
        LocalDate startDate = LocalDate.parse("2025-12-01");
        LocalDate endDate = LocalDate.parse("2025-12-17");
        String name = "Test Project";
        int id = 1;

        dbProject = new Project();
        dbProject.setLeadsList(defaultLeadsList);
        dbProject.setStart_date(startDate);
        dbProject.setEnd_date(endDate);
        dbProject.setName(name);
        dbProject.setId(id);

        newProject = new Project();
        newProject.setLeadsList(defaultLeadsList);
        newProject.setStart_date(startDate);
        newProject.setEnd_date(endDate);
        newProject.setName(name);
        newProject.setId(id);
    }

    @Test
    void shouldEditProjectName () {

        String newName = "New Name";
        dbProject.setName(newName);

        when(projectRepository.updateProject(dbProject)).thenReturn(dbProject);

        when(accountRepository.getAccountByMail(leadAccount.getMail())).thenReturn(leadAccount);

        assertEquals(newName, this.projectService.updateProject(dbProject).getName());
    }

    @Test
    void shouldEditProjectStartDate () {

        LocalDate newDate = LocalDate.parse("2025-11-01");

        dbProject.setStart_date(newDate);

        when(projectRepository.updateProject(dbProject)).thenReturn(dbProject);

        when(accountRepository.getAccountByMail(leadAccount.getMail())).thenReturn(leadAccount);

        assertEquals(newDate, this.projectService.updateProject(dbProject).getStart_date());
    }

    @Test
    void shouldEditProjectEndDate () {

        LocalDate newDate = LocalDate.parse("2026-01-01");

        dbProject.setEnd_date(newDate);

        when(projectRepository.updateProject(dbProject)).thenReturn(dbProject);

        when(accountRepository.getAccountByMail(leadAccount.getMail())).thenReturn(leadAccount);

        assertEquals(newDate, this.projectService.updateProject(dbProject).getEnd_date());
    }

    @Test
    void shouldEditProjectLead () {

        Employee newEmployee = new Employee();
        newEmployee.setMail("newLead@alphasolutions.com");

        Account newLead = new Account();
        newLead.setEmployee(newEmployee);
        newLead.setRole(Role.PROJECT_LEAD);
        newLead.setId(2);

        List<Account> newLeadList = List.of(newLead);

        dbProject.setLeadsList(newLeadList);

        when(projectRepository.updateProject(dbProject)).thenReturn(dbProject);

        when(accountRepository.getAccountByMail(newLead.getMail())).thenReturn(newLead);

        assertEquals(newLeadList, this.projectService.updateProject(dbProject).getLeadsList());
    }

    @Test
    void editProjectShouldRejectProjectWithInvalidName () {

        String invalidName = "  ";

        dbProject.setName(invalidName);

        InvalidFieldException exception = assertThrows(InvalidFieldException.class, () -> this.projectService.updateProject(dbProject));

        assertEquals("invalid name", exception.getMessage());
        assertEquals(Field.TITLE.getValue(), exception.getField());
    }

    @Test
    void editProjectShouldRejectProjectWithEndDateInThePast () {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        dbProject.setEnd_date(yesterday);

        InvalidDateException exception = assertThrows(InvalidDateException.class, () -> this.projectService.updateProject(dbProject));

        assertEquals("End date cannot be in the past!", exception.getMessage());
        assertEquals(Field.DATE.getValue(), exception.getField());
        assertEquals(2, exception.getErrorId());
    }

    @Test
    void editProjectShouldRejectProjectWithEndDateBeforeStartDate () {

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = startDate.minusDays(1);

        dbProject.setStart_date(startDate);
        dbProject.setEnd_date(endDate);

        InvalidDateException exception = assertThrows(InvalidDateException.class, () -> this.projectService.updateProject(dbProject));

        assertEquals("Start date cannot be after end date!", exception.getMessage());
        assertEquals(Field.DATE.getValue(), exception.getField());
        assertEquals(1, exception.getErrorId());
    }

    @Test
    void editProjectShouldNotInsertToJunctionIfUpdateProjectFails () {

        when(projectRepository.updateProject(any(Project.class))).thenThrow(RuntimeException.class);

        when(accountRepository.getAccountByMail(leadAccount.getMail())).thenReturn(leadAccount);

        assertThrows(RuntimeException.class, () -> this.projectService.updateProject(dbProject));

        verify(projectRepository).updateProject(dbProject);
        verify(projectRepository, never()).deleteFromAccountProjectJunction(anyInt());
        verify(projectRepository, never()).insertIntoAccountProjectJunction(anyInt(), anyInt());
    }

    @Test
    void shouldDeleteProject() {

        when(projectRepository.deleteProject(dbProject)).thenReturn(1);

        assertDoesNotThrow(() -> this.projectService.deleteProject(dbProject));

        verify(projectRepository).deleteProject(dbProject);

    }

}