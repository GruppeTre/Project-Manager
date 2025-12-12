package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/")
public class HomeController {

    private final AccountService accountService;
    private final ProjectService projectService;
    private final View view;

    public HomeController(AccountService accountService, ProjectService projectService, View view){
        this.accountService = accountService;
        this.projectService = projectService;
        this.view = view;
    }

    @GetMapping
    public String getLogin(Model model){
        Employee employee = new Employee();
        Account account = new Account();

        account.setEmployee(employee);

        model.addAttribute("account", account);

        return "index";
    }
    @PostMapping("/login")
    public String login(Model model, HttpSession session, HttpServletResponse response, @ModelAttribute Account account){

        if(!accountService.accountLogin(account)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("account", account);
            return "index";
        }

        account = accountService.getAccountByMail(account.getMail());

        session.setAttribute("account", account);
        String redirect = "redirect:/overview";

        Account roleId = (Account) session.getAttribute("account");
        if(SessionUtils.userHasRole(session, Role.ADMIN)) {
            String viewMode = "?viewMode=accounts";

            redirect = redirect.concat(viewMode);

            return redirect;
        }
        else{
            String viewMode = "?viewMode=projects";
            redirect = redirect.concat(viewMode);

            return redirect;
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/overview")
    public String getOverviewPage(@RequestParam(value = "viewMode", required = false) String viewMode, HttpSession session, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        String viewModeContainer = viewMode;

        if(viewMode == null || viewMode.isEmpty() && ((Account) session.getAttribute("account")).getRole() == Role.ADMIN){
            viewModeContainer = "accounts";
        }
        if(viewMode == null || viewMode.isEmpty() && ((Account) session.getAttribute("account")).getRole() == Role.PROJECT_LEAD){
            viewModeContainer = "projects";
        }

        model.addAttribute("viewMode", viewModeContainer);
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("session", session.getAttribute("account"));

        if(viewModeContainer.equals("projects") && SessionUtils.userHasRole(session, Role.ADMIN)){
            model.addAttribute("projects", projectService.getProjects());
        }
        if(viewModeContainer.equals("projects") && SessionUtils.userHasRole(session, Role.PROJECT_LEAD)){
            int projectLeadId = ((Account) session.getAttribute("account")).getId();
            model.addAttribute("projectsByLead", projectService.getProjectsByLead(projectLeadId));
        }

        return "overviewPage";
    }
}
