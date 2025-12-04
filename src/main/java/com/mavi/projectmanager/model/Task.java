package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private LocalDate start_date;
    private LocalDate end_date;
    private int duration; //in hours

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name) && Objects.equals(start_date, task.start_date) && Objects.equals(end_date, task.end_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, start_date, end_date, duration);
    }
}
