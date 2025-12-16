package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Integer estimatedDuration; //in hours
    private Integer actualDuration; //in hours
    private Integer archived;
    private List<Account> accountList;

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

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(Integer actualDuration) {
        this.actualDuration = actualDuration;
    }

    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }


    public int percentageDuration() {
        // Return 0 when actualDuration is missing or zero to avoid / by zero.
        if (actualDuration == null || actualDuration == 0) {
            return 0;
        }
        double percentage = (actualDuration * 100.0) / estimatedDuration;
        return (int) Math.round(percentage);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(estimatedDuration, task.estimatedDuration) && Objects.equals(name, task.name) && Objects.equals(startDate, task.startDate) && Objects.equals(endDate, task.endDate) && Objects.equals(description, task.description) && Objects.equals(accountList, task.accountList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, description, estimatedDuration, accountList);
    }

    //Jens Gotfredsen
    public String accountsToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < accountList.size(); i++) {
            String fullName = accountList.get(i).getFirstName() + " " + accountList.get(i).getLastName();

            stringBuilder.append(fullName);

            if(i < accountList.size() - 1){
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }
}
