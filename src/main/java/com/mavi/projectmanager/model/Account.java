package com.mavi.projectmanager.model;

public class Account {

    private int id;
    private Role role;
    private String password;
    private Employee employee;

    public int getId() {
        return id;
    }

    public void setEmployee(Employee emp) {
        this.employee = emp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirstName() {
        return employee.getFirstName();
    }

    public String getLastName() {
        return employee.getLastName();
    }

    public String getMail() {
        return employee.getMail();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
