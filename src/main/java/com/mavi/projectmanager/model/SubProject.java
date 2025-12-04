package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class SubProject {
    private int id;
    private String name;
    private LocalDate start_date;
    private LocalDate end_date;
    private List<Task> taskList;

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

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SubProject that = (SubProject) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(start_date, that.start_date) && Objects.equals(end_date, that.end_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, start_date, end_date);
    }
}
