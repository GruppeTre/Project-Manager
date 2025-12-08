package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.SubProject;
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


    public SubProject createSubProject(SubProject subProject, int projectId) {

        subProject.setName(subProject.getName().trim());

        DateUtils.validateDates(subProject.getStart_date(), subProject.getEnd_date());

        this.subProjectRepository.createSubProject(subProject, projectId);

        return subProject;
    }

    public void deleteSubProjectById(int subProjectId) {

        int rowsAffected = this.subProjectRepository.deleteSubProjectById(subProjectId);

        if (rowsAffected != 1) {
            throw new IllegalArgumentException("Unexpected number of subprojects with id: " + subProjectId
                    + " found in database! Expected: [1], actual: [" + rowsAffected + "]");
        }
    }
}
