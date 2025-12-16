package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidDateException;
import com.mavi.projectmanager.exception.InvalidFieldException;
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

    public Task getTask(int id) {
        return this.taskRepository.getTaskById(id);
    }

    //Jens Gotfredsen
    @Transactional
    public Task createTask(Task task, SubProject subProject) {

        trimFields(task);

        validateFields(task, subProject);

        Task newTask = taskRepository.createTask(task, subProject);

        taskRepository.addEmployeesToTaskJunction(task);

        return newTask;
    }

    //Magnus Sørensen
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

    public void archiveTask(Task task) {
        int rowsAffected = taskRepository.archiveTask(task);
        if(rowsAffected != 1){
            throw new IllegalArgumentException("An unexpected number of projects with id: " + task.getId()
                    + " found in database! Expected: [1], found: [" + rowsAffected + "]");
        }
    }

    //Magnus Sørensen
    public void deleteTask(Task toDelete) {

        int rowsAffected = taskRepository.deleteTask(toDelete);

        if (rowsAffected != 1) {
            throw new IllegalArgumentException("Unexpected number of tasks with id: " + toDelete.getId()
                    + " found in database! Expected: [1], actual: [" + rowsAffected + "]");
        }
    }

    //Magnus Sørensen
    private void trimFields(Task task) {
        task.setName(task.getName().trim());
        task.setDescription(task.getDescription().trim());
    }

    //Magnus Sørensen
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

    //Jens Gotfredsen
    private void validateDates(Task taskToCheck, SubProject subProjectToCompare) {

        if (taskToCheck.getStartDate().isAfter(taskToCheck.getEndDate())) {
            throw new InvalidDateException("Task start date cannot be after end date!", 3);
        }

        if (taskToCheck.getEndDate().isBefore(taskToCheck.getStartDate())) {
            throw new InvalidDateException("Task end date cannot be before start date!", 4);
        }

        if (taskToCheck.getStartDate().isBefore(subProjectToCompare.getStartDate())) {
            throw new InvalidDateException("Task start date cannot be before subproject start date!", 5);
        }

        if (taskToCheck.getStartDate().isAfter(subProjectToCompare.getEndDate())) {
            throw new InvalidDateException("Task start date cannot be after subproject end date!", 6);
        }
        if (taskToCheck.getEndDate().isBefore(subProjectToCompare.getStartDate())) {
            throw new InvalidDateException("Task end date cannot be before subproject start date!", 7);
        }

        if (taskToCheck.getEndDate().isAfter(subProjectToCompare.getEndDate())) {
            throw new InvalidDateException("Task end date cannot be after subproject end date", 8);
        }
    }
}
