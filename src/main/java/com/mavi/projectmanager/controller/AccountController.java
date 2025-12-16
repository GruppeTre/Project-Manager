package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/account")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    //Shows the createUSerPage
    //Jacob Klitgaard
    @GetMapping("/create")
    public String getCreateUserPage(HttpSession session, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        Account newAccount = new Account();
        Employee employee = new Employee();
        newAccount.setEmployee(employee);

        model.addAttribute("account", newAccount);
        model.addAttribute("roles", Role.values());

        return "createUserPage";
    }

    //Creates a new account
    //Jacob Klitgaard
    @PostMapping("/create")
    public String createNewUser(HttpSession session, Model model, @ModelAttribute Account newAccount, HttpServletResponse response) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Check to see if all fields are filled correctly
        try{
            service.createUser(newAccount);
        } catch (InvalidFieldException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getField());
            model.addAttribute("newAccount", newAccount);
            model.addAttribute("roles", Role.values());
            return "createUserPage";
        }

        return "redirect:/overview?viewMode=accounts";
    }

    //Jens Gotfredsen
    @GetMapping("/edit/{id}")
    public String getEditUser(HttpSession session, @PathVariable int id, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        Account currentUser = (Account) session.getAttribute("account");
        if (currentUser.getRole() != Role.ADMIN) {
            return "redirect:/overview?viewMode=accounts";
        }

        Account account = service.getAccountByID(id);

        model.addAttribute("account", account);
        model.addAttribute("roles", Role.values());

        return "editUserPage";
    }

    //Jens Gotfredsen
    @PostMapping("/editUser")
    public String editUser(HttpSession session, @ModelAttribute Account updatedAccount) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.ADMIN)) {
            return "redirect:/overview?viewMode=projects";
        }

        service.updatedAccount(updatedAccount);

        return "redirect:/overview?viewMode=accounts";
    }

    //Magnus Sørensen
    @PostMapping("/deleteUser")
    public String deleteUser(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute Account toDelete) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.ADMIN)) {
            return "redirect:/overview?viewMode=projects";
        }

        toDelete.setId(this.service.getAccountByMail(toDelete.getMail()).getId());

        try {
            toDelete = this.service.deleteAccount(toDelete);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("adminDeletion", true);
            return "redirect:/overview?viewMode=accounts";
        }

        if (toDelete == null) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("undefined", true);
        } else {
            redirectAttributes.addFlashAttribute("success", true);
        }

        return "redirect:/overview?viewMode=accounts";
    }

    //Jens Gotfredsen
    @GetMapping("/generatePassword")
    @ResponseBody
    public String generatePassword(HttpSession session) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!SessionUtils.userHasRole(session, Role.ADMIN)) {
            return "redirect:/";
        }

        return service.generatePassword();
    }
}
