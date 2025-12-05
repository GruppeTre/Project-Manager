package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @MockitoBean
    private Model model;

    private Project testProject;
    private List<Project> projectList;
    private List<Account> projectLeads;
    private Account leadAccount;
    private Account adminAccount;
    private Project emptyProject;

    @BeforeEach
    void setUp() {
        Employee leadEmp = new Employee();
        leadEmp.setMail("lead@alphasolutions.com");
        leadEmp.setFirstName("Erik");
        leadEmp.setLastName("Eriksen");
        leadAccount = new Account();
        leadAccount.setEmployee(leadEmp);
        leadAccount.setRole(Role.PROJECT_LEAD);
        leadAccount.setId(2);

        Employee adminEmp = new Employee();
        adminEmp.setMail("admin@alphasolutions.com");
        adminAccount = new Account();
        adminAccount.setEmployee(adminEmp);
        adminAccount.setRole(Role.ADMIN);
        adminAccount.setId(1);

        testProject = new Project();
        testProject.setId(1);
        testProject.setName("Test Project");
        testProject.setStart_date(LocalDate.of(2025, 11, 28));
        testProject.setEnd_date(LocalDate.of(2025, 11, 30));
        testProject.setLeadsList(List.of(leadAccount));

        projectList = new ArrayList<>();
        emptyProject = new Project();
        projectLeads = new ArrayList<>();

    }

/*
    ======================================
    =            GET TESTS            =
    ======================================
*/

    @Test
    void shouldShowProjectsAsAdmin() throws Exception {

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(get("/overview")
                        .sessionAttr("account", adminAccount)
                        .param("viewMode", "projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("overviewPage"))
                .andExpect(model().attribute("projects", projectList))
                .andExpect(model().attribute("viewMode", "projects"));
        mockedStatic.close();

        verify(projectService, times(1)).getProjects();
    }

    @Test
    void shouldShowProjectsAsProjectLead() throws Exception {

        List<Project> projects = Collections.singletonList(testProject);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        when(projectService.getProjectsByLead(leadAccount.getId())).thenReturn(projects);

        mockMvc.perform(get("/overview")
                        .sessionAttr("account", leadAccount)
                        .param("viewMode", "projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("overviewPage"))
                .andExpect(model().attribute("viewMode", "projects"))
                .andExpect(model().attribute("projectsByLead", projects));
            mockedStatic.close();
            verify(projectService, times(1)).getProjectsByLead(leadAccount.getId());
    }

    //todo: shouldShowEditProjectPage
    @Test
    void shouldShowEditProjectPage() throws Exception {

        when(projectService.getProjectById(testProject.getId())).thenReturn(testProject);

        //todo: replace with mock implementation of accountService.getAccountsByRole (when it is implemented)
        when(accountService.getAccountByID(anyInt())).thenReturn(leadAccount);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(get("/project/edit/{id}", testProject.getId())
                        .sessionAttr("account", adminAccount))
                .andExpect(status().isOk())
                .andExpect(view().name("editProjectPage"))
                .andExpect(model().attribute("project", testProject));
//                .andExpect(model().attribute("allLeads", projectLeads));
        mockedStatic.close();

        verify(projectService, times(1)).getProjectById(testProject.getId());
    }

    @Test
    void shouldShowCreateProjectPage() throws Exception {

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(get("/project/create")
                        .sessionAttr("account", adminAccount))
                .andExpect(status().isOk())
                .andExpect(view().name("createProjectPage"))
                .andExpect(model().attribute("project", emptyProject))
                .andExpect(model().attribute("allLeads", projectLeads));
        mockedStatic.close();

    }

/*
    ======================================
    =            POST TESTS            =
    ======================================
*/

    @Test
    void shouldEditValidProject() throws Exception {

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/update")
                        .sessionAttr("account", adminAccount))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/overview?viewMode=projects"));
        mockedStatic.close();

        verify(projectService).updateProject(any(Project.class));
    }


    @Test
    void editProjectPageShouldRejectProjectWithEndDateInThePast() throws Exception {

        when(projectService.updateProject(any(Project.class)))
                            .thenThrow(new InvalidDateException("End date cannot be in the past!", 2));

        //todo: replace with mock implementation of accountService.getAccountsByRole (when it is implemented)
        when(accountService.getAccountByID(anyInt())).thenReturn(leadAccount);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/update")
                        .sessionAttr("account", adminAccount)
                        .param("leadsList[0].employee.mail", leadAccount.getMail()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("editProjectPage"))
                .andExpect(model().attribute("errorId", 2))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("invalidField", Field.DATE.getValue()))
                .andExpect(model().attribute("project", Matchers.instanceOf(Project.class)))
                .andExpect(model().attribute("allLeads", Matchers.instanceOf(List.class)));
        mockedStatic.close();

        verify(projectService).updateProject(any(Project.class));
    }

    @Test
    void editProjectPageShouldRejectProjectWithEndDateBeforeStartDate() throws Exception {

        when(projectService.updateProject(any(Project.class)))
                .thenThrow(new InvalidDateException("End date cannot be before start date", 1));

        //todo: replace with mock implementation of accountService.getAccountsByRole (when it is implemented)
        when(accountService.getAccountByID(anyInt())).thenReturn(leadAccount);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/update")
                        .sessionAttr("account", adminAccount)
                        .param("leadsList[0].employee.mail", leadAccount.getMail()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("editProjectPage"))
                .andExpect(model().attribute("errorId", 1))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("invalidField", Field.DATE.getValue()))
                .andExpect(model().attribute("project", Matchers.instanceOf(Project.class)))
                .andExpect(model().attribute("allLeads", Matchers.instanceOf(List.class)));
        mockedStatic.close();

        verify(projectService).updateProject(any(Project.class));
    }

    @Test
    void shouldCreateValidProject() throws Exception {

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/create")
                        .sessionAttr("account", adminAccount))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/overview?viewMode=projects"));
        mockedStatic.close();

        verify(projectService).createProject(any(Project.class));
    }

    //todo: shouldRejectProjectWithEndDateInThePast

    @Test
    void shouldRejectProjectWithEndDateInThePast() throws Exception {

        when(projectService.createProject(any(Project.class)))
                .thenThrow(new InvalidDateException("End date cannot be in the past!", 2));

        when(accountService.getAccountByID(anyInt())).thenReturn(leadAccount);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/create")
                        .sessionAttr("account", adminAccount)
                        .param("leadsList[0].employee.mail", leadAccount.getMail()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createProjectPage"))
                .andExpect(model().attribute("errorId", 2))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("invalidField", Field.DATE.getValue()))
                .andExpect(model().attribute("project", Matchers.instanceOf(Project.class)))
                .andExpect(model().attribute("allLeads", Matchers.instanceOf(List.class)));
        mockedStatic.close();

        verify(projectService).createProject(any(Project.class));
    }

    @Test
    void shouldRejectProjectWithEndDateBeforeStartDate() throws Exception {

        when(projectService.createProject(any(Project.class)))
                .thenThrow(new InvalidDateException("End date cannot be before start date", 1));

        when(accountService.getAccountByID(anyInt())).thenReturn(leadAccount);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/create")
                        .sessionAttr("account", adminAccount)
                        .param("leadsList[0].employee.mail", leadAccount.getMail()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createProjectPage"))
                .andExpect(model().attribute("errorId", 1))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("invalidField", Field.DATE.getValue()))
                .andExpect(model().attribute("project", Matchers.instanceOf(Project.class)))
                .andExpect(model().attribute("allLeads", Matchers.instanceOf(List.class)));
        mockedStatic.close();

        verify(projectService).createProject(any(Project.class));
    }

    @Test
    void shouldDeleteProject() throws Exception {

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/delete")
                .sessionAttr("account", adminAccount))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/overview?viewMode=projects"));

        mockedStatic.close();
    }
}