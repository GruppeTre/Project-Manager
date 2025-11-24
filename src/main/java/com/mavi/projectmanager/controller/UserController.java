package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    private final AccountService;
    private final EmployeeService;

    @Autowired
    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    private boolean isLoggedIn = false;

    @GetMapping
    public showIndex(HttpSession session, Model model) {

        if (!sessionUtils.isloggedIn(session)) {
            List<Account> accounts = accountService.METHODTORETRIEVEACCOUNTS;
            model.addAttribute(accounts);
            return "index";
        }
        else {
            return "index";
        }
    }

}
