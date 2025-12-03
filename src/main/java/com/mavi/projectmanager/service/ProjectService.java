package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.ProjectRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;

    public ProjectService(ProjectRepository projectRepository, AccountRepository accountRepository) {
        this.projectRepository = projectRepository;
        this.accountRepository = accountRepository;
    }

    public List<Project> getProjects(){
        return projectRepository.getProjects();
    }

    public Project getProjectById(int id) {
        try {
            return this.projectRepository.getProjectById(id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("Failed to get exactly one result for project id " + id);
        }
    }

    public List<Project> getProjectsByLead(int id){
        return projectRepository.getProjectsByLead(id);
    }

    @Transactional
    public Project createProject(Project project, Employee employee) {

        //todo: validate project fields

        int projectId = this.projectRepository.createProject(project, employee);
        Account accountId = this.accountRepository.getAccountByMail(employee.getMail());

        this.projectRepository.insertIntoAccountProjectJunction(accountId.getId(), projectId);

        return project;
    }

    @Transactional
    public Project updateProject(Project project, Employee assignedLead) {

        project.setName(project.getName().trim());

        if (!hasValidName(project)) {
            throw new InvalidFieldException("invalid name", Field.TITLE);
        }

        validateDates(project);

        //update project data
        this.projectRepository.updateProject(project);
        Account leadAccount = this.accountRepository.getAccountByMail(assignedLead.getMail());

        //delete row(s) from junction table
        this.projectRepository.deleteFromAccountProjectJunction(project.getId());

        //insert new fields
        this.projectRepository.insertIntoAccountProjectJunction(leadAccount.getId(), project.getId());

        return project;
    }

    public List<Project> getFullProjectById(int id){
        return this.projectRepository.getFullProjectById(id);
    }

    private boolean hasValidName(Project projectToCheck) {

        if(projectToCheck.getName().isBlank()){
            return false;
        }

//        String regex = "^[a-zA-Z0-9 ]+$";

//        return projectToCheck.getName().matches(regex);

        return true;
    }

    private void validateDates(Project projectToCheck) {

        LocalDate today = LocalDate.now();

        if (projectToCheck.getStart_date().isAfter(projectToCheck.getEnd_date())) {
            throw new InvalidDateException("Start date cannot be after end date!", 1);
        }

        if (!projectToCheck.getEnd_date().isAfter(today)) {
            throw new InvalidDateException("End date cannot be in the past!", 2);
        }
    }

    private boolean hasProjectLead(Project projectToCheck) {
        return true;
    }
}
