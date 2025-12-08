package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.model.Task;
import com.mavi.projectmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createTask(Task task, SubProject subProject){

        taskRepository.createTask(task, subProject);

        taskRepository.addEmployeesToTaskJunction(task);

        return task;
    }
}
