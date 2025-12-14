package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Project {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int archived;
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

    public int getArchived() {
        return archived;
    }

    public void setArchived(int archived) {
        this.archived = archived;
    }

    public int sumProjectDuration(){
        int sum = 0;
        for(SubProject sb : subProjectsList){
            sum += sb.sumDuration();
        }

        return sum;
    }

    public int sumActualDuration(){
        int sum = 0;
        for(SubProject sp : subProjectsList){
            sum += sp.sumActualDuration();
        }

        return sum;
    }


    public int percentageDuration() {
        if (subProjectsList == null || subProjectsList.isEmpty()) {
            return 0;
        }

        long sumEstimatedDuration = 0;
        long sumActualDuration = 0;

        for (SubProject sp : subProjectsList) {
            if (sp == null || sp.getTaskList() == null){
                continue;
            }

            for (Task t : sp.getTaskList()) {
                if (t == null) {
                    continue;
                }

                Integer estimatedDuration = t.getEstimatedDuration();
                Integer actualDuration = t.getActualDuration();

                if (estimatedDuration != null && estimatedDuration >= 0) {
                    sumEstimatedDuration += estimatedDuration;
                }
                if (actualDuration != null && actualDuration >= 0) {
                    sumActualDuration += actualDuration;
                }
            }
        }

        if (sumActualDuration == 0) {
            return 0;
        }

        double percentage = (sumActualDuration * 100.0) / sumEstimatedDuration;
        return (int) Math.round(percentage);
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

    //Jens Gotfredsen
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
