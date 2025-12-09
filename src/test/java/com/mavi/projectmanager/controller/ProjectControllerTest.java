package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.model.*;
import com.mavi.projectmanager.service.*;
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
    private SubProjectService subProjectService;
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private TaskController taskController;
    @MockitoBean
    private HttpSession session;
    @MockitoBean
    private Model model;

    private Project testProject;
    private SubProject testSubProject;
    private List<Project> projectList;
    private List<Account> projectLeads;
    private Account leadAccount;
    private Account adminAccount;
    private Project emptyProject;
    private SubProject emptySubProject;

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

        testSubProject = new SubProject();
        testSubProject.setId(1);
        testSubProject.setName("Test SubProject");
        testSubProject.setStart_date(LocalDate.of(2025, 11, 28));
        testSubProject.setEnd_date(LocalDate.of(2025, 11, 30));

        projectList = new ArrayList<>();
        emptyProject = new Project();
        emptySubProject = new SubProject();
        projectLeads = new ArrayList<>();
    }

/*
    ======================================
    =            GET TESTS            =
    ======================================
*/


    @Test
    void shouldShowEditSubProjectPageWithFetchedSubProject() throws Exception {
        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        SubProject sp = new SubProject();
        when(subProjectService.getSubprojectById(4)).thenReturn(sp);


        mockMvc.perform(get("/project/edit-subproject/4")
                        .sessionAttr("account", adminAccount))
                .andExpect(status().isOk())
                .andExpect(view().name("editSubprojectPage"))
                .andExpect(model().attribute("subproject", sp));

        mockedStatic.close();

        verify(subProjectService, times(1)).getSubprojectById(4);

    }

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

    @Test
    void shouldShowCreateSubProjectPage() throws Exception {

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(get("/project/{projectId}/subProject/create", 1)
                        .sessionAttr("account", leadAccount))
                .andExpect(status().isOk())
                .andExpect(view().name("createSubProjectPage"))
                .andExpect(model().attribute("subProject", emptySubProject));
        mockedStatic.close();

    }


/*
    ======================================
    =            POST TESTS            =
    ======================================
*/
    /*
        ======================================
        =            POST TESTS            =
        ======================================
    */
    @Test
    void shouldUpdateProjectFromFormAsProjectLead() throws Exception {

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        SubProject sp = new SubProject();
        sp.setId(4);

        when(subProjectService.updateSubProject(Mockito.any(SubProject.class))).thenReturn(1);

        mockMvc.perform(post("/project/update-subproject")
                        .sessionAttr("account", adminAccount)
                        .param("id", "4")
                        .param("name", "Test SubProject")
                        .param("start_date", "2025-12-01")
                        .param("end_date", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("/editProjectPage"));

        mockedStatic.close();

        verify(subProjectService, times(1)).updateSubProject(Mockito.any(SubProject.class));
    }

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
    void shouldCreateValidSubProject() throws Exception {

        when(subProjectService.createSubProject(any(SubProject.class), eq(1))).thenReturn(new SubProject());

        mockMvc.perform(post("/project/{projectId}/subProject/create", 1)
                        .param("name", "My Sub Project")
                        .param("start_date", "2025-12-10")
                        .param("end_date",   "2025-12-31")
                        .sessionAttr("account", leadAccount)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/view/1"));

        verify(subProjectService).createSubProject(any(SubProject.class), eq(1));
    }

    @Test
    void shouldShowProjectOverviewPage() throws Exception {
        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);

        Project project = new Project();
        List<SubProject> subProjectList = new ArrayList<>();
        SubProject subProject = new SubProject();
        List<Task> taskList = new ArrayList<>();
        Task task = new Task();
        List<Account> employeeList = new ArrayList<>();

        project.setSubProjectsList(subProjectList);
        subProject.setTaskList(taskList);
        task.setAccountList(employeeList);


        when(projectService.getFullProjectById(anyInt())).thenReturn(project);

        mockMvc.perform(get("/project/view/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("projectOverviewPage"))
                .andExpect(model().attribute("project", project));

        mockedStatic.close();
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

    @Test
    void shouldDeleteTask() throws Exception {

        Task testTask = new Task();
        testTask.setId(1);
        when(taskService.getTask(testTask.getId())).thenReturn(testTask);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class))).thenReturn(true);
        mockedStatic.when(() -> SessionUtils.userHasRole(Mockito.any(HttpSession.class), any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/project/edit/{projectId}/task/delete", 1)
                        .sessionAttr("account", leadAccount).param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/view/" + 1))
                .andExpect(flash().attributeCount(0));
        mockedStatic.close();
    }
}