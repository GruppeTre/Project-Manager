package com.mavi.projectmanager.controller;
import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.*;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.ProjectService;
import com.mavi.projectmanager.service.SubProjectService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/project")
    public class ProjectController {

    private final ProjectService projectService;
    private final AccountService accountService;
    private final SubProjectService subProjectService;

    public ProjectController(ProjectService projectService, AccountService accountService, SubProjectService subProjectService) {
        this.projectService = projectService;
        this.accountService = accountService;
        this.subProjectService = subProjectService;
    }

        @GetMapping("/create")
        public String getCreateProjectPage(Model model) {
            Project project = new Project();

            List<Account> allLeads = accountService.getAccountsByRole(Role.PROJECT_LEAD);

            model.addAttribute("project", project);
            model.addAttribute("allLeads", allLeads);

            return "createProjectPage";
        }

        @PostMapping("/create")
        public String createProject(HttpSession session, Model model, @ModelAttribute Project newProject, @ModelAttribute Employee employee, HttpServletResponse response) {

            if(!SessionUtils.isLoggedIn(session)) {
                return "redirect:/";
            }

            if(!SessionUtils.userHasRole(session, Role.ADMIN)) {
                return "redirect:/";
            }

            try {
                projectService.createProject(newProject);
            } catch (InvalidFieldException e) {
                List<Account> allLeads = accountService.getAccountsByRole(Role.PROJECT_LEAD);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if (e instanceof InvalidDateException) {
                    int errorId = ((InvalidDateException) e).getErrorId();
                    model.addAttribute("errorId", errorId);
                }

                System.out.println("Invalid fields: " + e.getField());

                model.addAttribute("error", true);
                model.addAttribute("invalidField", e.getField());
                model.addAttribute("project", newProject);
                model.addAttribute("allLeads", allLeads);
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

        if(viewMode.equals("projects") && !SessionUtils.userHasRole(session, Role.PROJECT_LEAD)){
            model.addAttribute("projects", projectService.getProjects());
        }
        if(viewMode.equals("projects") && SessionUtils.userHasRole(session, Role.PROJECT_LEAD)){
            int projectLeadId = ((Account) session.getAttribute("account")).getId();
            model.addAttribute("projectsByLead", projectService.getProjectsByLead(projectLeadId));
        }
        return "overviewPage";

    }

    @GetMapping("/edit/{id}")
    public String getEditProjectPage(RedirectAttributes redirectAttributes, HttpSession session, @PathVariable int id, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if(!SessionUtils.userHasRole(session, Role.ADMIN)) {
            return "redirect:/overview?viewMode=projects";
        }

        Project toEdit;

        try {
            toEdit = this.projectService.getProjectById(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/overview?viewMode=projects";
        }

        List<Account> allLeads = accountService.getAccountsByRole(Role.PROJECT_LEAD);

        model.addAttribute("project", toEdit);
        model.addAttribute("allLeads", allLeads);

        return "editProjectPage";
    }

    @PostMapping("/update")
    public String updateProject(HttpServletResponse response, Model model, HttpSession session, @ModelAttribute Project project) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        if(!SessionUtils.userHasRole(session, Role.ADMIN)) {
            return "redirect:/overview?viewMode=projects";
        }

        try {
            this.projectService.updateProject(project);
        } catch (InvalidFieldException e) {
            List<Account> allLeads = accountService.getAccountsByRole(Role.PROJECT_LEAD);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            //if Exception is an InvalidDateException, add errorId to model so template can display proper error message
            if (e instanceof InvalidDateException) {
                int errorId = ((InvalidDateException) e).getErrorId();
                model.addAttribute("errorId", errorId);
            }
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getField());

            //add model attributes needed to display project information on template
            model.addAttribute("project", project);
            model.addAttribute("allLeads", allLeads);

            return "editProjectPage";
        }

        return "redirect:/overview?viewMode=projects";
    }

    @GetMapping("/view/{id}")
    public String getProjectOverview(@PathVariable("id") int id, Model model, HttpSession session) {
        if(!SessionUtils.isLoggedIn(session)){
            return "redirect:/";
        }

        model.addAttribute("project", projectService.getFullProjectById(id));

        return "projectOverviewPage";
    }

    @GetMapping("/{projectId}/subProject/create")
    public String getCreateSubProjectsPage(@PathVariable("projectId") int projectId, Model model) {
        SubProject subProject = new SubProject();

        model.addAttribute("subProject", subProject);
        model.addAttribute("projectId", projectId);

        return "createSubProjectPage";

    }

    @PostMapping("/{projectId}/subProject/create")
    public String createSubProject(HttpSession session, Model model, @ModelAttribute SubProject subProject, @PathVariable("projectId") Integer projectId, HttpServletResponse response) {

        if(!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        if(!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/";
        }


        try {
            subProjectService.createSubProject(subProject, projectId);
        } catch (InvalidFieldException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            if (e instanceof InvalidDateException) {
                int errorId = ((InvalidDateException) e).getErrorId();
                model.addAttribute("errorId", errorId);
            }

            System.out.println("Invalid fields: " + e.getField());

            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getField());
            model.addAttribute("subProject", subProject);
            return "createSubProjectPage";
        }

        return "redirect:/project/view/" + projectId;
    }
}


