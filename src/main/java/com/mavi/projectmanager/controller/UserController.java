package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final AccountService accountService;

    public UserController (AccountService accountService){
        this.accountService = accountService;
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
