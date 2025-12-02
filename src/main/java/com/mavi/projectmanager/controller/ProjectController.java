package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/overview")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public String getProjectOverviewPage(@RequestParam("viewMode") String viewMode, Model model, HttpSession session) {
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        if (viewMode.equals("projects")) {
            model.addAttribute("projects", projectService.getProjects());
            model.addAttribute("viewMode", viewMode);
        }
        return "overviewPage";

    }

    @PostMapping("/delete")
    public String deleteProjectByProject(HttpSession httpSession, @ModelAttribute Project toDelete, RedirectAttributes redirectAttributes) {

        if (!SessionUtils.isLoggedIn(httpSession)) {
            return "redirect:/";
        }
        //Reject user if user is not Admin
        Account currentUser = (Account) httpSession.getAttribute("account");
        if (currentUser.getRole() != Role.ADMIN) {
            return "redirect:/overview";
        }

        try {
            int rowsAffected = projectService.deleteProjectByProject(toDelete);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("admin-deletion", true);
            return "redirect:/overview";
        }

        if (toDelete == null) {
            redirectAttributes.addFlashAttribute("error", true);
        } else {
            redirectAttributes.addFlashAttribute("success", true);
        }
        return "redirect:/overviewPage";


    }
}
