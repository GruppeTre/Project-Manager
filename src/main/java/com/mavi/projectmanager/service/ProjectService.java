package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getProjects() {
        return projectRepository.getProjects();
    }

    public int deleteProjectByProject(Project toDelete) {
        //Check if Object id has the value 0 or below 0.
        if (toDelete == null || toDelete.getId() <= 0) {
            throw new IllegalArgumentException("Invalid project provided for deletion.");
        }

        // Fetch the latest project from the repository to validate it exists
        Project existingProject = projectRepository.getProjectById(toDelete.getId());
        if (existingProject == null) {
            // Project not found
            return 0;
        }
        //Validate that the project to be deleted matches the project designated by id in toDelete - perform deletion.
        if (Objects.equals(toDelete.getName(), existingProject.getName())
                && Objects.equals(toDelete.getStart_date(), existingProject.getStart_date())
                && Objects.equals(toDelete.getEnd_date(), existingProject.getEnd_date())) {
            return projectRepository.deleteProjectByProject(existingProject);
        }
        else {
            throw new IllegalStateException("Project details on this project does not match a project in the database");

        }
    }

}
