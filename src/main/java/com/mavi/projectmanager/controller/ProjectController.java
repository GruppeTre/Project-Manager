package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/overview")
public class ProjectController {
    private final ProjectService projectService;
    private final AccountService accountService;

    public ProjectController(ProjectService projectService, AccountService accountService){
        this.projectService = projectService;
        this.accountService = accountService;
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
}
