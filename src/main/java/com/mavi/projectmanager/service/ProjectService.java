package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;

    public ProjectService(ProjectRepository projectRepository, AccountRepository accountRepository) {
        this.projectRepository = projectRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Project createProject(Project project, Employee employee) {

        int projectId = this.projectRepository.createProject(project, employee);
        Account accountId = this.accountRepository.getAccountByMail(employee.getMail());

        this.projectRepository.updateAccountProjectJunction(accountId.getId(), projectId);

        return project;
    }

    public boolean hasValidName(Project projectToCheck) {

        if(projectToCheck.getName().isBlank()){
            return false;
        }

        projectToCheck.setName(projectToCheck.getName().trim());

        String regex = "^[a-zA-Z0-9 ]+$";

        return projectToCheck.getName().matches(regex);
    }

    public void validateDates(Project projectToCheck) {

        LocalDate today = LocalDate.now();

        if (!projectToCheck.getStart_date().isAfter(today)) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        if (projectToCheck.getStart_date().isAfter(projectToCheck.getEnd_date())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (!projectToCheck.getEnd_date().isAfter(today)) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }
    }

    public boolean hasProjectLead(Project projectToCheck) {
        return true;
    }

    public List<Project> getProjects(){
        return projectRepository.getProjects();
    }


}
