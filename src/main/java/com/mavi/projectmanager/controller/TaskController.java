package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.*;
import com.mavi.projectmanager.repository.EmployeeRepository;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.SubProjectService;
import com.mavi.projectmanager.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final EmployeeRepository employeeRepository;
    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final TaskService taskService;
    private final SubProjectService subProjectService;

    public TaskController(EmployeeRepository employeeRepository, AccountService accountService, EmployeeService employeeService, TaskService taskService, SubProjectService subProjectService) {
        this.employeeRepository = employeeRepository;
        this.accountService = accountService;
        this.employeeService = employeeService;
        this.taskService = taskService;
        this.subProjectService = subProjectService;
    }

    @GetMapping("/{projectId}/{subProjectId}/create")
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

    @PostMapping("/create")
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

        return "redirect:/project/view/" + projectId;
    }
}
