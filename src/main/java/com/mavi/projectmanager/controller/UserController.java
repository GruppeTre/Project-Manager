package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/")
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

    @GetMapping("/overview")
    public String getOverviewPage(HttpSession session, Model model) {

        /*if (!sessionUtils.isloggedIn(session)) {
            return "redirect:/login";
            }
        }*/
        model.addAttribute("users", service.getAllAccounts());

        return "overviewPage";
    }
  
    @GetMapping("/edit/{id}")
    public String getEditUser(@PathVariable int id, Model model, HttpSession httpSession){
        Account account = service.getAccountByID(id);

        model.addAttribute("account", account);
        model.addAttribute("roles", Role.values());

        return "editUserPage";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute Account updatedAccount){
        service.updatedAccount(updatedAccount);

        return "redirect:/user/edit/1";
    }
}
