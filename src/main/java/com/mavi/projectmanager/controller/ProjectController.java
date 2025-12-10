package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.*;
import com.mavi.projectmanager.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final AccountService accountService;
    private final SubProjectService subProjectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, AccountService accountService, SubProjectService subProjectService, TaskService taskService) {
        this.projectService = projectService;
        this.accountService = accountService;
        this.subProjectService = subProjectService;
        this.taskService = taskService;
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

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        if (!SessionUtils.userHasRole(session, Role.ADMIN)) {
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

    @GetMapping("/edit/{id}")
    public String getEditProjectPage(RedirectAttributes redirectAttributes, HttpSession session, @PathVariable int id, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.ADMIN)) {
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

        if (!SessionUtils.userHasRole(session, Role.ADMIN)) {
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
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        model.addAttribute("project", projectService.getFullProjectById(id));

        return "projectOverviewPage";
    }

    @GetMapping("/{projectId}/create")
    public String getCreateSubProjectsPage(@PathVariable("projectId") int projectId, Model model) {
        SubProject subProject = new SubProject();

        model.addAttribute("subProject", subProject);
        model.addAttribute("projectId", projectId);

        return "createSubProjectPage";

    }

    @PostMapping("/{projectId}/create")
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

    @PostMapping("/delete")
    public String deleteProject(HttpSession session, @ModelAttribute Project toDelete, RedirectAttributes redirectAttributes) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.ADMIN)) {
            return "redirect:/overview?viewMode=projects";
        }

        try {
            projectService.deleteProject(toDelete);
        } catch (Exception e) {
            return "redirect:/overview?viewMode=projects";
        }

        return "redirect:/overview?viewMode=projects";
    }

    @PostMapping("/{projectId}/subProject/{subProjectId}/delete")
    public String deleteSubProject(@PathVariable int projectId, @PathVariable int subProjectId, HttpSession session, RedirectAttributes redirectAttributes) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not project lead
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/overview?viewMode=projects";
        }

        SubProject toDelete = subProjectService.getSubProjectById(subProjectId);

        System.out.println("id: " + toDelete.getId());

        try {
            subProjectService.deleteSubProject(toDelete);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", true);
        }

        //return to project view
        return "redirect:/project/view/" + projectId;
    }

    @PostMapping("/edit/{projectId}/task/delete")
    public String deleteTask(@PathVariable int projectId, @ModelAttribute Task toDelete, HttpSession session, RedirectAttributes redirectAttributes) {

        System.out.println("entering endpoint");

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/overview?viewMode=projects";
        }

        //todo: check if lead is actual owner of project and task?

        toDelete = taskService.getTask(toDelete.getId());

        System.out.println("got task with id: " + toDelete.getId());
        try {
            projectService.deleteTask(toDelete);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", true);
        }

        //return to project view
        return "redirect:/project/view/" + projectId;
    }

    @GetMapping("/edit/{projectId}/{subProjectId}")
    public String editSubproject(@PathVariable("projectId") int projectId, @PathVariable("subProjectId") int subProjectId, Model model, HttpSession session) {
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not project lead
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/overview?viewMode=projects";
        }

        SubProject toEdit;

        Project project = projectService.getProjectById(projectId);

        try {
            toEdit = subProjectService.getSubprojectById(subProjectId);

            System.out.println("start date: " + toEdit.getStart_date());
        } catch (IllegalArgumentException i) {
            //ToDO: add flash attribute
            return "redirect/overView?viewMode=projects";
        }

        model.addAttribute("subProject", toEdit);
        model.addAttribute("project", project);

        return "editSubprojectPage";
    }

    @PostMapping("/edit/{projectId}/{subProjectId}") /// ::::
    public String updateSubproject(@PathVariable("projectId") int projectId, @PathVariable("subProjectId") int subProjectId, @ModelAttribute SubProject toUpdate, HttpSession session, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/overviewPage";
        }

        //Reject user if user is not project lead
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/overview?viewMode=projects";
        }

        model.addAttribute("project", toUpdate); // ensures Thymeleaf has it

       Project project = projectService.getProjectById(projectId);

        try {
            subProjectService.updateSubProject(toUpdate, project);

        } catch (InvalidFieldException e) {

            if (e instanceof InvalidDateException) {
                int errorId = ((InvalidDateException) e).getErrorId();
                model.addAttribute("errorId", errorId);
            }
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getField());
            model.addAttribute("subProject", toUpdate);
            model.addAttribute("project", project);

            return "editSubProjectPage";
        }

        //SHOULD USER BE REDIRECTED TO EDITPROJECTPAGE?
        return "redirect:/project/view/" + projectId;

        //ToDO: add RedirectAttributes for whether the update was successful. - redirect to the same page and return the same object with it.
    }

    @GetMapping("/{projectId}/subproject/{subprojectId}/task/{taskId}/edit")
    public String getEditTaskPage(HttpSession session, @PathVariable("projectId") int projectId, @PathVariable("subprojectId") int subProjectId,
                         @PathVariable int taskId, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/overviewPage";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/overview?viewMode=projects";
        }

        Task toEdit = taskService.getTask(taskId);
        toEdit.setAccountList(this.accountService.getAccountsAssignedToTask(toEdit.getId()));

        List<Account> teamMembers = accountService.getAccountsByRole(Role.TEAM_MEMBER);
        SubProject subProject = subProjectService.getSubProjectById(subProjectId);

        model.addAttribute("task", toEdit);
        model.addAttribute("subproject", subProject);
        model.addAttribute("teamMembers", teamMembers);

        return "editTaskPage";
    }

    @PostMapping("/{projectId}/subproject/{subprojectId}/task/edit")
    public String editTask(@RequestParam("employeeList") List<String> accountList, @PathVariable int projectId, @PathVariable int subprojectId, HttpSession session,
                           @ModelAttribute Task toEdit, Model model, HttpServletResponse response) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/overviewPage";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/overview?viewMode=projects";
        }

        //set assigned accounts on task:

        List<Account> assignedAccounts = new ArrayList<>();

        for(String mail : accountList){
            Account account = accountService.getAccountByMail(mail);
            assignedAccounts.add(account);
        }

        toEdit.setAccountList(assignedAccounts);

        SubProject subProject = subProjectService.getSubProjectById(subprojectId);

        try {
            this.taskService.updateTask(toEdit, subProject);
        } catch (InvalidFieldException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            List<Account> teamMembers = accountService.getAccountsByRole(Role.TEAM_MEMBER);

            //model attributes necessary for displaying error box
            if (e instanceof InvalidDateException) {
                int errorId = ((InvalidDateException) e).getErrorId();
                model.addAttribute("errorId", errorId);
            }
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getField());

            //model attributes necessary for displaying form contents
            model.addAttribute("task", toEdit);
            model.addAttribute("subproject", subProject);
            model.addAttribute("teamMembers", teamMembers);

            return "editTaskPage";
        }

        return "redirect:/project/view/" + projectId;
    }
}


