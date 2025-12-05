package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private LocalDate start_date;
    private LocalDate end_date;
    private String description;
    private int estimatedDuration; //in hours
    private int actualDuration;
    // TODO: Remember to swap this to account in sprint 2
    private List<Employee> employeeList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && estimatedDuration == task.estimatedDuration && Objects.equals(name, task.name) && Objects.equals(start_date, task.start_date) && Objects.equals(end_date, task.end_date) && Objects.equals(description, task.description) && Objects.equals(employeeList, task.employeeList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, start_date, end_date, description, estimatedDuration, employeeList);
    }

    public String employeesToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < employeeList.size(); i++) {
            String fullName = employeeList.get(i).getFirstName() + " " + employeeList.get(i).getLastName();

            stringBuilder.append(fullName);

            if(i < employeeList.size() - 1){
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }
}
