package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            }
        }*/
        model.addAttribute("users", accountService.getAllAccounts());

        return "overviewPage";
    }
  
    @GetMapping("/edit/{id}")
    public String getEditUser(@PathVariable int id, Model model, HttpSession httpSession){
        Account account = accountService.getAccountByID(id);

        model.addAttribute("account", account);
        model.addAttribute("roles", Role.values());

        return "editUserPage";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute Account updatedAccount){
        accountService.updatedAccount(updatedAccount);

        return "redirect:/user/edit/1";
    }
}
