package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.repository.SubProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;

    public SubProjectService(SubProjectRepository subProjectRepository) {
        this.subProjectRepository = subProjectRepository;
    }

    public SubProject getSubProjectById(int id){
        return subProjectRepository.getSubProjectById(id);
    }
}
