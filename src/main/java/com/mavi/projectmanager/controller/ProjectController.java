package com.mavi.projectmanager.controller;
import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Controller
    @RequestMapping("/overview")
    public class ProjectController {

    private final ProjectService projectService;
    private final AccountService accountService;
    private final EmployeeService employeeService;

    public ProjectController(ProjectService projectService, AccountService accountService, EmployeeService employeeService) {
            this.projectService = projectService;
        this.accountService = accountService;
        this.employeeService = employeeService;
    }

        @GetMapping("project/create")
        public String getCreateProjectPage(Model model) {
            Project project = new Project();

            List<Employee> employees = employeeService.getEmployeesByRole();
            Employee employee = new Employee();

            model.addAttribute("project", project);
            model.addAttribute("employees", employees);
            model.addAttribute("employee", employee);

            return "createProjectPage";
        }

        @PostMapping("project/create")
        public String createProject(HttpSession session, Model model, @ModelAttribute Project newProject, @ModelAttribute Account account, HttpServletResponse response) {

            if(!SessionUtils.isLoggedIn(session)) {
                return "redirect:/";
            }

            try {
                projectService.createProject(newProject, account);

            } catch(InvalidFieldException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    model.addAttribute("error", true);
                    model.addAttribute("InvalidField", e.getField());
                    model.addAttribute("newproject", newProject);
                    List<Employee> employees = employeeService.getEmployeesByRole();
                    model.addAttribute("employees", employees);
                    return "createProjectPage";
                }

                return "redirect:/overviewPage";
            }

    @GetMapping("/projects")
    public String getProjectOverviewPage(@RequestParam("viewMode") String viewMode, Model model, HttpSession session){
        if(!SessionUtils.isLoggedIn(session)){
            return "redirect:/";
        }

        if(viewMode.equals("projects")){
            model.addAttribute("projects", projectService.getProjects());
            model.addAttribute("accounts", accountService.getAccounts());
            model.addAttribute("viewMode", viewMode);
        }
        return "overviewPage";

    }
        }


