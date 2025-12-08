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

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createTask(Task task, SubProject subProject){

        if(!hasValidName(task)){
            throw new InvalidFieldException("Name cannot be blank", Field.TITLE);
        }
        if(!hasValidEmployees(task)){
            throw new InvalidFieldException("Employees cannot be empty", Field.EMPLOYEE);
        }
        if(!hasValidEstimation(task)){
            throw new InvalidFieldException("Estimation cannot be empty", Field.ESTIMATION);
        }

        validateDates(task, subProject);

        Task newTask = taskRepository.createTask(task, subProject);

        taskRepository.addEmployeesToTaskJunction(task);

        return newTask;
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

    private boolean hasValidName(Task taskToCheck) {
        return !taskToCheck.getName().isBlank();

    }

    private boolean hasValidEmployees(Task taskToCheck){
        return !taskToCheck.getAccountList().isEmpty();
    }

    private boolean hasValidEstimation(Task taskToCheck){
        Integer estimation = taskToCheck.getEstimatedDuration();

        return estimation != null && estimation > 0;
    }
}
