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
@RequestMapping("/")
public class UserController {
    private final AccountService service;

    public UserController(AccountService service) {
        this.service = service;
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

        if(!service.accountLogin(account)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("account", account);
            return "index";
        }

        account = service.getAccountByMail(account.getMail());

        session.setAttribute("account", account);
        String redirect = "redirect:/overview";

        Account roleId = (Account) session.getAttribute("account");
        if(roleId.getRole().getId() == 1) {
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

    //Shows the createUSerPage
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

        return "redirect:/overview";
    }

    @GetMapping("/overview")
    public String getOverviewPage(@RequestParam("viewMode") String viewMode, HttpSession session, Model model) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        if(viewMode.equals("accounts")) {
            model.addAttribute("accounts", service.getAccounts());
            model.addAttribute("session", session.getAttribute("account"));
            model.addAttribute("viewMode", viewMode);
        }

        return "overviewPage";
    }
  
    @GetMapping("/edit/{id}")
    public String getEditUser(@PathVariable int id, Model model, HttpSession httpSession){

        if (!SessionUtils.isLoggedIn(httpSession)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        Account currentUser = (Account) httpSession.getAttribute("account");
        if (currentUser.getRole() != Role.ADMIN) {
            return "redirect:/overview";
        }

        Account account = service.getAccountByID(id);

        model.addAttribute("account", account);
        model.addAttribute("roles", Role.values());

        return "editUserPage";
    }

    @PostMapping("/editUser")
    public String editUser(HttpSession session, @ModelAttribute Account updatedAccount){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!sessionUserIsAdmin(session)) {
            return "redirect:/overview";
        }

        service.updatedAccount(updatedAccount);

        return "redirect:/overview";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute Account toDelete) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Reject user if user is not Admin
        if (!sessionUserIsAdmin(session)) {
            return "redirect:/overview";
        }

        //Hello! This a safety measure to prevent the ID tampering with html document?
        toDelete.setId(this.service.getAccountByMail(toDelete.getMail()).getId());

        try {
            toDelete = this.service.deleteAccount(toDelete);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("admin-deletion", true);
            return "redirect:/overview";
        }

        // todo: redirect attributes to show feedback on operation on overview page
        if (toDelete == null) {
            redirectAttributes.addFlashAttribute("error", true);
        } else {
            redirectAttributes.addFlashAttribute("success", true);
        }

        return "redirect:/overview";
    }

    private boolean sessionUserIsAdmin(HttpSession session) {
        return ((Account)session.getAttribute("account")).getRole() == Role.ADMIN;
    }
}
