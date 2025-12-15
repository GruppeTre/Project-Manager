package com.mavi.projectmanager.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class SubProject {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
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

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    //Jens Gotfredsen
    public int sumDuration(){
        int sum = 0;
        for(Task d : taskList){
            sum += d.getEstimatedDuration();
        }

        return sum;
    }

    public int sumActualDuration(){
        int sum = 0;
        for(Task t : taskList){
            if(t.getActualDuration() != null || t.getActualDuration() != 0 ) {
                sum += t.getActualDuration();
            }
        }

        return sum;
    }



    public int percentageDuration() {
        if (taskList == null || taskList.isEmpty()) {
            return 0;
        }

        int sumEstimatedDuration = 0;
        int sumActualDuration = 0;

        for (Task t : taskList) {
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

        if (sumActualDuration == 0) {
            return 0;
        }

        double percentage = (sumActualDuration * 100.0) / sumEstimatedDuration;
        return (int) Math.round(percentage);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SubProject that = (SubProject) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate);
    }
}
