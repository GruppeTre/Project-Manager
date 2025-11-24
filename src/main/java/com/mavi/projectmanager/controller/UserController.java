package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.service.AccountService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mavi.projectmanager.controller.utils.SessionUtils;


@Controller
@RequestMapping("/account")
public class UserController {
    private final AccountService service;

    public UserController(AccountService service) {
        this.service = service;
    }

    //Shows the createUSerPage
    @GetMapping("/create")
    public String getCreateUserPage(HttpSession session, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        Account accountToAdd = new Account();
        model.addAttribute("accountToAdd", accountToAdd);

        return "createUserPage";
    }

    //Creates a new account
    @PostMapping("/create")
    public String createNewUser(HttpSession session, Model model, @ModelAttribute Account newAccount, HttpServletResponse response) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Check to see if all fields are filled correctly
        try{
            newAccount = service.createUser(newAccount);
            session.setAttribute("account", newAccount);
        } catch (InvalidFieldException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("InvalidField", e.getField());
            model.addAttribute("newUser", newAccount);
            return "createUserPage";
        }

        return "redirect:/createUserPage";
    }
}