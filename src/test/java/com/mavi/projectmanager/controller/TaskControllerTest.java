package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.model.*;
import com.mavi.projectmanager.repository.EmployeeRepository;
import com.mavi.projectmanager.service.*;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private SubProjectService subProjectService;
    @MockitoBean
    private ProjectService projectService;
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Test
    void shouldShowCreateTaskPage() throws Exception{

        SubProject subProject = new SubProject();
        Project project = new Project();
        project.setId(1);
        List<Account> teamList = new ArrayList<>();

        when(subProjectService.getSubProjectById(anyInt())).thenReturn(subProject);
        when(accountService.getAccountsByRole(Role.TEAM_MEMBER)).thenReturn(teamList);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class)))
                .thenReturn(true);

        Task task = new Task();

        mockMvc.perform(get("/task/{projectId}/{subProjectId}/create", 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("createTaskPage"))
                .andExpect(model().attribute("task", task))
                .andExpect(model().attribute("subProject", subProject))
                .andExpect(model().attribute("projectId", project.getId()))
                .andExpect(model().attribute("teamList", teamList));

        mockedStatic.close();
    }
}
