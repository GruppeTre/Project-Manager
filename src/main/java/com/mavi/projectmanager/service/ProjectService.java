package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.*;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.ProjectRepository;
import com.mavi.projectmanager.service.utils.DateUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;

    public ProjectService(ProjectRepository projectRepository, AccountRepository accountRepository) {
        this.projectRepository = projectRepository;
        this.accountRepository = accountRepository;
    }

    //Magnus Sørensen
    public List<Project> getProjects() {
        return projectRepository.getProjects();
    }

    //Magnus Sørensen
    public Project getProjectById(int id) {
        try {
            return this.projectRepository.getProjectById(id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("Failed to get exactly one result for project id " + id);
        }
    }

    //Magnus Sørensen
    public List<Project> getProjectsByLead(int id) {
        return projectRepository.getProjectsByLead(id);
    }

    //Jacob Klitgaard
    @Transactional
    public Project createProject(Project project) {

        project.setName(project.getName().trim());

        if (!hasValidName(project)) {
            throw new InvalidFieldException("Invalid name", Field.TITLE);
        }

        DateUtils.validateDates(project.getStartDate(), project.getEndDate());

        int projectId = this.projectRepository.createProject(project);
        int accountId = this.accountRepository.getAccountByMail(project.getLeadsList().getFirst().getMail()).getId();

        this.projectRepository.insertIntoAccountProjectJunction(accountId, projectId);

        return project;
    }

    //Magnus Sørensen
    @Transactional
    public Project updateProject(Project project) {

        //validate project (returns project with full Account object of project lead)
        validateUpdatedProject(project);

        int leadId = project.getLeadsList().getFirst().getId();

        //update project data
        this.projectRepository.updateProject(project);

        //delete row(s) from junction table
        this.projectRepository.deleteFromAccountProjectJunction(project.getId());

        //insert new fields
        this.projectRepository.insertIntoAccountProjectJunction(leadId, project.getId());

        return project;
    }

    //Jens Gotfredsen
    public Project getFullProjectById(int id) {
        return this.projectRepository.getFullProjectById(id);
    }

    //Emil Gurresø
    public void deleteProject(Project toDelete) {

        int rowsAffected = projectRepository.deleteProject(toDelete);

        //Signal, on whether the database is corrupt.
        if (rowsAffected != 1) {
            throw new IllegalArgumentException("Multiple lists was found with this id: " + toDelete.getId() +
                    ", and it is unclear what Project to delete. Please contact dataspecialist");
        }
    }

    //Jens Gotfredsen
    public List<Project> getProjectByTeamMember(int id) {
        return projectRepository.getProjectByTeamMember(id);
    }

    public void archiveProject(Project project){
        int rowsAffected = projectRepository.archiveProject(project);
        if(rowsAffected != 1){
            throw new IllegalArgumentException("An unexpected number of projects with id: " + project.getId()
                    + " found in database! Expected: [1], found: [" + rowsAffected + "]");
        }
    }

    public List<Project> getArchivedProjects() {
        return projectRepository.getArchivedProjects();
    }

    //Jacob Klitgaard
    private boolean hasValidName(Project projectToCheck) {
        return !projectToCheck.getName().isBlank();
    }

    //Magnus Sørensen
    private Project validateUpdatedProject(Project project) {

        //trim name for leading and trailing whitespaces
        project.setName(project.getName().trim());

        //validate name
        if (!hasValidName(project)) {
            throw new InvalidFieldException("invalid name", Field.TITLE);
        }

        DateUtils.validateDates(project.getStartDate(), project.getEndDate());

        //validate that project lead exists
        String mail = project.getLeadsList().getFirst().getMail();
        Account lead = accountRepository.getAccountByMail(mail);

        if (lead == null) {
            throw new IllegalArgumentException("Lead with mail: '" + mail + "' does not exist!");
        }

        //validate that Account is of role Project Lead
        if (lead.getRole() != Role.PROJECT_LEAD) {
            throw new IllegalArgumentException("Unexpected role of assigned project lead: expected: " + Role.PROJECT_LEAD.getValue() + " actual: " + lead.getRole().getValue());
        }

        project.setLeadsList(List.of(lead));

        return project;
    }
}
