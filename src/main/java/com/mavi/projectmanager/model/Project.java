package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Project {
    private int id;
    private String name;
    private LocalDate start_date;
    private LocalDate end_date;
    private List<Account> leadsList;
    private List<SubProject> subProjectsList;


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

    public List<Account> getLeadsList() {
        return leadsList;
    }

    public void setLeadsList(List<Account> leadsList) {
        this.leadsList = leadsList;
    }

    public List<SubProject> getSubProjectsList() {
        return subProjectsList;
    }

    public void setSubProjectsList(List<SubProject> subProjectsList) {
        this.subProjectsList = subProjectsList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id && Objects.equals(name, project.name) && Objects.equals(start_date, project.start_date) && Objects.equals(end_date, project.end_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, start_date, end_date);
    }

    public String leadsToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < leadsList.size(); i++) {
            String fullName = leadsList.get(i).getFirstName() + " " + leadsList.get(i).getLastName();

            stringBuilder.append(fullName);

            if(i < leadsList.size() - 1){
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }
}
