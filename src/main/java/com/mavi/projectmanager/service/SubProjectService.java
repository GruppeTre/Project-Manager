package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.ProjectRepository;
import com.mavi.projectmanager.repository.SubProjectRepository;
import com.mavi.projectmanager.service.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;
    private final ProjectRepository projectRepository;

    public SubProjectService(SubProjectRepository subProjectRepository, ProjectRepository projectRepository) {
        this.subProjectRepository = subProjectRepository;
        this.projectRepository = projectRepository;
    }


    public SubProject createSubProject(SubProject subProject, int projectId) {

        subProject.setName(subProject.getName().trim());

        DateUtils.validateDates(subProject.getStart_date(), subProject.getEnd_date());

        this.subProjectRepository.createSubProject(subProject, projectId);

        return subProject;
    }
}
