package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private ProjectService projectService;
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private HttpSession session;

    private Project testProject;
    private List<Project> projectList;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1);
        testProject.setName("Test Project");
        testProject.setStart_date(LocalDate.of(2025, 11, 28));
        testProject.setEnd_date(LocalDate.of(2025, 11, 30));

        projectList = new ArrayList<>();

    }

    @Test
    void shouldShowProjects() throws Exception {
        Employee testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setPosition("Lead Developer");
        testEmployee.setFirstName("Peter");
        testEmployee.setLastName("Petersen");
        testEmployee.setMail("pepe@company.com");

        Account testAccount = new Account();
        testAccount.setId(1);
        testAccount.setRole(Role.ADMIN);
        testAccount.setPassword("1234");
        testAccount.setEmployee(testEmployee);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class)))
                .thenReturn(true);

        mockMvc.perform(get("/projects").sessionAttr("account", testAccount).param("viewMode", "projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("overviewPage"))
                .andExpect(model().attribute("projects", projectList))
                .andExpect(model().attribute("viewMode", "projects"));

        mockedStatic.close();
    }

    //todo: shouldShowCreateProjectPage

    //todo: shouldCreateValidProject

    //todo: shouldRejectProjectWithEndDateInThePast

    //todo: shouldRejectProjectWithEndDateBeforeStartDate
}