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

    public int updateSubProject(SubProject subProject) {
        return subProjectRepository.updateSubProject(subProject);
    }
}
