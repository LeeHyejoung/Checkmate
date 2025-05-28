package com.security.CheckMate.Domain;

public class User {
    String userName;
    String status;
    String[] subjects;

    public User(String userName, String status, String[] subjects) {
        this.userName = userName;
        this.status = status;
        this.subjects = subjects;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public void setSubjects(String[] subjects) {
        this.subjects = subjects;
    }
}
