package com.mavi.projectmanager.model;

import java.time.LocalDate;

public class Project {

    private int id;
    private String name;
    private LocalDate start_date;
    private LocalDate end_date;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }
}
