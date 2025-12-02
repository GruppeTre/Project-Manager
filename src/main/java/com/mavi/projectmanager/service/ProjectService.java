package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    public Project createProject(Project project, Account account) {
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
