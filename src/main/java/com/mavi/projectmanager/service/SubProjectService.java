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

    public SubProject getSubProjectById(int id) {
        return subProjectRepository.getSubProjectById(id);
    }

    public SubProject createSubProject(SubProject subProject, int projectId) {

        subProject.setName(subProject.getName().trim());

        DateUtils.validateDates(subProject.getStart_date(), subProject.getEnd_date());

        this.subProjectRepository.createSubProject(subProject, projectId);

        return subProject;
    }

    public void deleteSubProject(SubProject toDelete) {

        int rowsAffected = this.subProjectRepository.deleteSubProject(toDelete);

        if (rowsAffected != 1) {
            throw new IllegalArgumentException("Unexpected number of subprojects with id: " + toDelete
                    + " found in database! Expected: [1], actual: [" + rowsAffected + "]");
        }
    }

    public SubProject getSubprojectById(int id) {
        return subProjectRepository.getSubprojectById(id);
    }

    public int updateSubProject(SubProject subProject, Project project) {

        subProject.setName(subProject.getName().trim());
        validateFields(subProject, project);

        return subProjectRepository.updateSubProject(subProject);
    }

    private void validateFields(SubProject subProject, Project project) {

        boolean invalidName = subProject.getName().isBlank();

        if (invalidName) {
            throw new InvalidFieldException("Name cannot be blank", Field.TITLE);
        }

        validateDates(subProject, project);
    }

    private void validateDates(SubProject subProjectToCheck, Project projectToCompare) {
        if (subProjectToCheck.getStart_date().isAfter(subProjectToCheck.getEnd_date())) {
            throw new InvalidDateException("Subproject start date cannot be after end date!", 3);
        }

        if (subProjectToCheck.getEnd_date().isBefore(subProjectToCheck.getStart_date())) {
            throw new InvalidDateException("Subproject end date cannot be before start date!", 4);
        }

        if (subProjectToCheck.getStart_date().isBefore(projectToCompare.getStart_date())) {
            throw new InvalidDateException("Subproject start date cannot be before project start date!", 5);
        }

        if (subProjectToCheck.getStart_date().isAfter(projectToCompare.getEnd_date())) {
            throw new InvalidDateException("Subproject start date cannot be after project end date!", 6);
        }
        if (subProjectToCheck.getEnd_date().isBefore(projectToCompare.getStart_date())) {
            throw new InvalidDateException("Subproject end date cannot be before project start date!", 7);
        }

        if (subProjectToCheck.getEnd_date().isAfter(projectToCompare.getEnd_date())) {
            throw new InvalidDateException("Subproject end date cannot be after project end date", 8);
        }
    }
}
