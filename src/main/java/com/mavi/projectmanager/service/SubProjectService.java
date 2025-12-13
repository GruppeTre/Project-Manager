package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.model.Task;
import com.mavi.projectmanager.repository.ProjectRepository;
import com.mavi.projectmanager.repository.SubProjectRepository;
import com.mavi.projectmanager.service.utils.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;

    public SubProjectService(SubProjectRepository subProjectRepository) {
        this.subProjectRepository = subProjectRepository;
    }

    //Jens Gotfredsen
    public SubProject getSubProjectById(int id) {
        return subProjectRepository.getSubProjectById(id);
    }

    //Jacob Klitgaard
    public SubProject createSubProject(SubProject subProject, int projectId) {

        subProject.setName(subProject.getName().trim());

        DateUtils.validateDates(subProject.getStartDate(), subProject.getEndDate());

        this.subProjectRepository.createSubProject(subProject, projectId);

        return subProject;
    }

    //Jacob Klitgaard
    public void deleteSubProject(SubProject toDelete) {

        int rowsAffected = this.subProjectRepository.deleteSubProject(toDelete);

        if (rowsAffected != 1) {
            throw new IllegalArgumentException("Unexpected number of subprojects with id: " + toDelete
                    + " found in database! Expected: [1], actual: [" + rowsAffected + "]");
        }
    }

    //Jacob Klitgaard
    public SubProject getSubprojectById(int id) {
        return subProjectRepository.getSubprojectById(id);
    }

    //Jacob Klitgaard
    public int updateSubProject(SubProject subProject, Project project) {

        subProject.setName(subProject.getName().trim());
        validateFields(subProject, project);

        return subProjectRepository.updateSubProject(subProject);
    }

    //Jacob Klitgaard
    private void validateFields(SubProject subProject, Project project) {

        boolean invalidName = subProject.getName().isBlank();

        if (invalidName) {
            throw new InvalidFieldException("Name cannot be blank", Field.TITLE);
        }

        validateDates(subProject, project);
    }

    //Jacob Klitgaard
    private void validateDates(SubProject subProjectToCheck, Project projectToCompare) {
        if (subProjectToCheck.getStartDate().isAfter(subProjectToCheck.getEndDate())) {
            throw new InvalidDateException("Subproject start date cannot be after end date!", 3);
        }

        if (subProjectToCheck.getEndDate().isBefore(subProjectToCheck.getStartDate())) {
            throw new InvalidDateException("Subproject end date cannot be before start date!", 4);
        }

        if (subProjectToCheck.getStartDate().isBefore(projectToCompare.getStartDate())) {
            throw new InvalidDateException("Subproject start date cannot be before project start date!", 5);
        }

        if (subProjectToCheck.getStartDate().isAfter(projectToCompare.getEndDate())) {
            throw new InvalidDateException("Subproject start date cannot be after project end date!", 6);
        }
        if (subProjectToCheck.getEndDate().isBefore(projectToCompare.getStartDate())) {
            throw new InvalidDateException("Subproject end date cannot be before project start date!", 7);
        }

        if (subProjectToCheck.getEndDate().isAfter(projectToCompare.getEndDate())) {
            throw new InvalidDateException("Subproject end date cannot be after project end date", 8);
        }
    }
}
