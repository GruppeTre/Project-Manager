package com.mavi.projectmanager.model;

import java.util.Comparator;
import java.util.Objects;

public class Account {

    private int id;
    private Role role;
    private String password;
    private Employee employee;
    public static final Comparator<Account> ACCOUNT_COMPARABLE = Comparator.comparing(((Account a) -> a.getRole().getId())).thenComparing(Account::getFirstName).thenComparing(Account::getLastName);

    public int getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
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

    public int getEmployeeId() {
        return getEmployeeId();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && role == account.role && Objects.equals(password, account.password) && Objects.equals(employee, account.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, password, employee);
    }
}
