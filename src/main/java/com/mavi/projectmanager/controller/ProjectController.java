package com.mavi.projectmanager.controller;
import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;


@Controller

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

            List<Account> accounts = accountService.getAccountsByRole(Role.PROJECT_LEAD);
            Account account = new Account();

            model.addAttribute("project", project);
            model.addAttribute("accounts", accounts);
            model.addAttribute("account", account);

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
                    List<Account> accounts = accountService.getAccountsByRole(Role.PROJECT_LEAD);
                    model.addAttribute("accounts", accounts);
                    return "createProjectPage";
                }

                return "redirect:/overview?viewMode=projects";
        }

    @GetMapping("/projects")
    public String getProjectOverviewPage(@RequestParam("viewMode") String viewMode, Model model, HttpSession session){
        if(!SessionUtils.isLoggedIn(session)){
            return "redirect:/";
        }

        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("viewMode", viewMode);

        if(viewMode.equals("projects") && !SessionUtils.userIsProjectLead(session)){
            model.addAttribute("projects", projectService.getProjects());
        }
        if(viewMode.equals("projects") && SessionUtils.userIsProjectLead(session)){
            int projectLeadId = ((Account) session.getAttribute("account")).getId();
            model.addAttribute("projectsByLead", projectService.getProjectsByLead(projectLeadId));
        }
        return "overviewPage";

    }

    @GetMapping("/project/edit/{id}")
    public String getEditProjectPage(RedirectAttributes redirectAttributes, HttpSession session, @PathVariable int id, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        Account currentUser = (Account) session.getAttribute("account");
        if (currentUser.getRole() != Role.ADMIN) {
            return "redirect:/overview";
        }

        Project toEdit;

        try {
            toEdit = this.projectService.getProjectById(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/projects?viewMode=projects";
        }

        List<Account> allLeads = accountService.getAccountsByRole(Role.PROJECT_LEAD);

        model.addAttribute("project", toEdit);
        model.addAttribute("allLeads", allLeads);
        model.addAttribute("assignedLead", new Employee());
        //todo: add list of assigned leads (or fix project object to contain list of assigned leads)


        return "editProjectPage";
    }

    @PostMapping("/project/update")
    public String updateProject(HttpServletResponse response, Model model, HttpSession session, @ModelAttribute Project project, @ModelAttribute Employee assignedLead) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        Account currentUser = (Account) session.getAttribute("account");
        if (currentUser.getRole() != Role.ADMIN) {
            return "redirect:/overview";
        }

        try {
            this.projectService.updateProject(project, assignedLead);
        } catch (InvalidFieldException e) {
            List<Account> allLeads = accountService.getAccountsByRole(Role.PROJECT_LEAD);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            if (e instanceof InvalidDateException) {
                int errorId = ((InvalidDateException) e).getErrorId();
                model.addAttribute("errorId", errorId);
            }

            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getField());
            model.addAttribute("project", project);
            model.addAttribute("allLeads", allLeads);
            model.addAttribute("assignedLead", assignedLead);

            System.out.println("returning with invalid field: " + e.getField());
            return "editProjectPage";
        }

        return "redirect:/projects?viewMode=projects";
    }
}


