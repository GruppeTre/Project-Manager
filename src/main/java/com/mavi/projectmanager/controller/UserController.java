package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
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

//        if (!SessionUtils.isLoggedIn(session)) {
//            return "redirect:/";
//        }

        Account newAccount = new Account();
        Employee employee = new Employee();
        model.addAttribute("account", newAccount);
        model.addAttribute("employee", employee);
        model.addAttribute("roles", Role.values());

        return "createUserPage";
    }

    //Creates a new account
    @PostMapping("/create")
    public String createNewUser(HttpSession session, Model model, @ModelAttribute Account newAccount, @ModelAttribute Employee employee,
                                HttpServletResponse response) {

//        if (!SessionUtils.isLoggedIn(session)) {
//            return "redirect:/";
//        }

        //Check to see if all fields are filled correctly
        try{
            service.createUser(newAccount, employee.getMail());

        } catch (InvalidFieldException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("InvalidField", e.getField());
            model.addAttribute("newAccount", newAccount);
            model.addAttribute("employee", employee);
            return "createUserPage";
        }

        return "redirect:/account/create";
    }
}