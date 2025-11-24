package com.mavi.projectmanager.model;

public enum Role {
    ADMIN (1, "Admin"),
    PROJECT_LEAD (2, "Project Lead"),
    TEAM_MEMBER (3, "Team Member");

    private int id;
    private String value;

    Role (int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
