package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.exception.Field;
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


@Controller
@RequestMapping("/account")
public class UserController {
    private final AccountService service;

    public UserController(AccountService service) {
        this.service = service;
    }


    //Shows the createUSerPage
    @GetMapping("/create")
    public String getCreateUserPage(Model model) {
        Account accountToAdd = new Account();
        model.addAttribute("accountToAdd", accountToAdd);

        return "createUserPage";
    }


    //Creates a new account
    @PostMapping("/create")
    public String createNewUser(HttpSession session, Model model, @ModelAttribute Account newAccount, HttpServletResponse response) {

        //Check to see if all fields are filled correctly
        try{
            newAccount = service.createAccount(newAccount);
            session.setAttribute("account", newAccount);
        } catch (InvalidFieldsException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getIncorrectField());
            model.addAttribute("newUser", newAccount);
            return "createUserPage";
        }

        return "redirect:/createUserPage";
    }
}