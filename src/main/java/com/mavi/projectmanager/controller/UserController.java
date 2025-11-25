package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UserController {

    private AccountService accountService;

    private ProjectService projectService;

    //Note: there can only be one constructor, as Spring only wants a single autowiring per class.
    public UserController(AccountService accountService) {
        this.accountService = accountService;
        this.projectService = projectService;
    }

    @GetMapping("/overview")
    public String getOverviewPage(HttpSession session, Model model) {

        /*if (!sessionUtils.isloggedIn(session)) {
            return "redirect:/login";
        }*/
        model.addAttribute("users", accountService.getAllAccounts());

        return "overviewPage";
    }
}
