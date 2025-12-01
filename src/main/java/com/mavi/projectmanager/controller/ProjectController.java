package com.mavi.projectmanager.controller;
import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.http.HttpResponse;


@Controller
    @RequestMapping("/project")
    public class ProjectController {

        private final ProjectService service;

        public ProjectController(ProjectService service) {
            this.service = service;
        }

        @GetMapping("/create")
        public String getCreateProjectPage(Model model) {
            Project project = new Project();
            Account account = new Account();
            Employee employee = new Employee();

            account.setEmployee(employee);

            model.addAttribute("project", project);

            return "createProjectPage";
        }

        @PostMapping("/create")
        public String createProject(HttpSession session, Model model, @ModelAttribute Project newProject, @ModelAttribute Account account, HttpServletResponse response) {

            if(!SessionUtils.isLoggedIn(session)) {
                return "redirect:/";
            }

            try {
                service.createProject(newProject, account);

            } catch(InvalidFieldException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    model.addAttribute("error", true);
                    model.addAttribute("InvalidField", e.getField());
                    model.addAttribute("newproject", newProject);
                    return "createProjectPage";
                }

                return "redirect:/overviewPage";
            }
        }
