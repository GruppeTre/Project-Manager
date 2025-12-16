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

    //Jacob Klitgaard
    @GetMapping("/create")
    public String getCreateProjectPage(Model model) {
        Project project = new Project();

        List<Account> allLeads = accountService.getAccountsByRole(Role.PROJECT_LEAD);

        model.addAttribute("project", project);
        model.addAttribute("allLeads", allLeads);

        return "createProjectPage";
    }

    //Jacob Klitgaard
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

    //Jacob Klitgaard & Magnus Sørensen
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

    //Jacob Klitgaard og Magnus Sørensen
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

    //Jens Gotfredsen
    @GetMapping("/view/{id}")
    public String getProjectOverview(@PathVariable("id") int id, @RequestParam( value = "viewMode", required=false) String viewMode, Model model, HttpSession session) {
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        model.addAttribute("project", projectService.getFullProjectById(id));
        model.addAttribute("session", session.getAttribute("account"));
        model.addAttribute("viewMode", viewMode);

        return "projectOverviewPage";
    }

    @GetMapping("/{projectId}/task/{taskId}/archive")
    public String archiveTask(@PathVariable("projectId") int projectId, @PathVariable("taskId") int taskId, HttpSession session, Model model){
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/project/view/" + projectId;
        }

        model.addAttribute("task", taskService.getTask(taskId));

        return "closeTaskPage";
    }

    @PostMapping("/{projectId}/task/{taskId}/archive")
    public String archiveTask(@PathVariable("projectId") int projectId, @PathVariable("taskId") int taskId, HttpSession session, @ModelAttribute Task task){
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }
        if (!SessionUtils.userHasRole(session, Role.PROJECT_LEAD)) {
            return "redirect:/project/view/" + projectId;
        }

        Task taskToArchive = taskService.getTask(taskId);
        taskToArchive.setActualDuration(task.getActualDuration());

        taskService.archiveTask(taskToArchive);

        return "redirect:/project/view/" + projectId + "?viewMode=project";
    }

    //Jacob Klitgaard
    @GetMapping("/{projectId}/create")
    public String getCreateSubProjectsPage(@PathVariable("projectId") int projectId, Model model) {
        SubProject subProject = new SubProject();

        model.addAttribute("subProject", subProject);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.getProjectById(projectId));

        return "createSubProjectPage";

    }

    //Jacob KLitgaard
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
            model.addAttribute("project", projectService.getProjectById(projectId));
            model.addAttribute("subProject", subProject);
            return "createSubProjectPage";
        }

        return "redirect:/project/view/" + projectId + "?viewMode=project";
    }

    //Emil Gurresø
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

    //Jacob Klitgaard
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

        return "redirect:/project/view/" + projectId + "?viewMode=project";
    }

    //Magnus Sørensen
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

        toDelete = taskService.getTask(toDelete.getId());

        System.out.println("got task with id: " + toDelete.getId());
        try {
            projectService.deleteTask(toDelete);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", true);
        }

        //return to project view
        return "redirect:/project/view/" + projectId + "?viewMode=project";
    }

    //Emil Gurresø
    @GetMapping("/edit/{projectId}/{subProjectId}")
    public String editSubproject(@PathVariable("projectId") int projectId, @PathVariable("subProjectId") int subProjectId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
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
        } catch (IllegalArgumentException i) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("undefined", true);
            return "redirect:/overview?viewMode=projects";
        }

        model.addAttribute("subProject", toEdit);
        model.addAttribute("project", project);

        return "editSubprojectPage";
    }

    //Jacob Klitgaard
    @PostMapping("/edit/{projectId}/{subProjectId}")
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

            return "editSubprojectPage";
        }

        return "redirect:/project/view/" + projectId + "?viewMode=project";
    }

    //Magnus Sørensen
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

    //Magnus Sørensen
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

        return "redirect:/project/view/" + projectId + "?viewMode=project";
    }

    //Jens Gotfredsen
    @GetMapping("/{projectId}/subproject/{subProjectId}/task/create")
    public String createTask(@PathVariable int subProjectId, @PathVariable int projectId, Model model, HttpSession session){
        if(!SessionUtils.isLoggedIn(session)){
            return "redirect:/";
        }

        SubProject subProject = subProjectService.getSubProjectById(subProjectId);

        Task task = new Task();
        List<Account> teamList = accountService.getAccountsByRole(Role.TEAM_MEMBER);

        model.addAttribute("task", task);
        model.addAttribute("subProject", subProject);
        model.addAttribute("projectId", projectId);
        model.addAttribute("teamList", teamList);

        return "createTaskPage";
    }

    //Jens Gotfredsen
    @PostMapping("/{projectId}/subproject/{subProjectId}/task/create")
    public String createTask(@RequestParam("employeeList") List<String> accountList, @RequestParam("retrievedProjectId") int projectId, @RequestParam("retrievedSubProjectId") int subProjectId, @ModelAttribute Task task, HttpSession session, Model model){
        if(!SessionUtils.isLoggedIn(session)){
            return "redirect:/";
        }

        List<Account> accountsList = new ArrayList<>();

        SubProject subProject = subProjectService.getSubProjectById(subProjectId);

        for(String list : accountList){
            Account account = accountService.getAccountByMail(list);
            accountsList.add(account);
        }

        task.setAccountList(accountsList);

        try {
            taskService.createTask(task, subProject);
        } catch (InvalidFieldException e){

            if(e instanceof InvalidDateException){
                int errorId = ((InvalidDateException) e).getErrorId();
                model.addAttribute(errorId);
            }

            List<Account> teamList = accountService.getAccountsByRole(Role.TEAM_MEMBER);

            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getField());
            model.addAttribute("task", task);
            model.addAttribute("subProject", subProject);
            model.addAttribute("projectId", projectId);
            model.addAttribute("teamList", teamList);

            return "createTaskPage";
        }

        return "redirect:/project/view/" + projectId + "?viewMode=project";
    }

    @PostMapping("/{id}/archive")
    public String archiveProject(@PathVariable("id") int id, HttpSession session){
        if(!SessionUtils.isLoggedIn(session)){
            return "redirect:/";
        }

        if(!SessionUtils.userHasRole(session, Role.ADMIN)){
            return "redirect:/overview?viewMode=projects";
        }

        Project projectToArchive = projectService.getProjectById(id);

        projectService.archiveProject(projectToArchive);

        return "redirect:/overview?viewMode=projects";
    }
}


