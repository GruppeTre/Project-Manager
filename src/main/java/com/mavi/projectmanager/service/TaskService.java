package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Project;
import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.model.Task;
import com.mavi.projectmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubProjectService subProjectService;

    public TaskService(TaskRepository taskRepository, SubProjectService subProjectService) {
        this.taskRepository = taskRepository;
        this.subProjectService = subProjectService;
    }

    public Task getTask(int id) {
        return this.taskRepository.getTaskById(id);
    }

    @Transactional
    public Task createTask(Task task, SubProject subProject){

        trimFields(task);

        validateFields(task, subProject);

        Task newTask = taskRepository.createTask(task, subProject);

        taskRepository.addEmployeesToTaskJunction(task);

        return newTask;
    }

    @Transactional
    public Task updateTask(Task task, SubProject subProject) {

        trimFields(task);

        validateFields(task, subProject);

        //update task
        taskRepository.updateTask(task);

        //delete row(s) from junction table
        taskRepository.deleteFromEmployeesToTaskJunction(task.getId());

        //insert new rows
        taskRepository.addEmployeesToTaskJunction(task);

        return task;
    }

    private void trimFields(Task task) {
        task.setName(task.getName().trim());
        task.setDescription(task.getDescription().trim());
    }

    private void validateFields(Task task, SubProject subProject) {

        boolean invalidName = task.getName().isBlank();
        boolean invalidDescription = task.getDescription().isBlank();
        boolean hasNoAssignedEmployees = task.getAccountList().isEmpty();
        boolean invalidEstimation = task.getEstimatedDuration() == null || task.getEstimatedDuration() <= 0;

        if (invalidName) {
            throw new InvalidFieldException("Name cannot be blank", Field.TITLE);
        }

        if (invalidDescription) {
            throw new InvalidFieldException("Description cannot be empty", Field.DESCRIPTION);
        }

        if (hasNoAssignedEmployees) {
            throw new InvalidFieldException("Employees cannot be empty", Field.EMPLOYEE);
        }

        if (invalidEstimation) {
            throw new InvalidFieldException("Estimation cannot be empty", Field.ESTIMATION);
        }

        validateDates(task, subProject);
    }

    private void validateDates(Task taskToCheck, SubProject subProjectToCompare) {
        if (taskToCheck.getStart_date().isAfter(taskToCheck.getEnd_date())) {
            throw new InvalidDateException("Task start date cannot be after end date!", 3);
        }

        if (taskToCheck.getEnd_date().isBefore(taskToCheck.getStart_date())) {
            throw new InvalidDateException("Task end date cannot be before start date!", 4);
        }

        if (taskToCheck.getStart_date().isBefore(subProjectToCompare.getStart_date())) {
            throw new InvalidDateException("Task start date cannot be before subproject start date!", 5);
        }

        if (taskToCheck.getStart_date().isAfter(subProjectToCompare.getEnd_date())) {
            throw new InvalidDateException("Task start date cannot be after subproject end date!", 6);
        }
        if (taskToCheck.getEnd_date().isBefore(subProjectToCompare.getStart_date())) {
            throw new InvalidDateException("Task end date cannot be before subproject start date!", 7);
        }

        if (taskToCheck.getEnd_date().isAfter(subProjectToCompare.getEnd_date())) {
            throw new InvalidDateException("Task end date cannot be after subproject end date", 8);
        }
    }
}
