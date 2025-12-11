package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Project {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public int sumProjectDuration(){
            int sum = 0;
            for(SubProject sb : subProjectsList){
                sum += sb.sumDuration();
            }

            return sum;
        }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id && Objects.equals(name, project.name) && Objects.equals(startDate, project.startDate) && Objects.equals(endDate, project.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate);
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
