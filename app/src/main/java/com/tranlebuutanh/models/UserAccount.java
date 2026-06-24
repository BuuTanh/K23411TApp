package com.tranlebuutanh.models;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String userName;
    private String password;
    private String role;
    private String displayName;
    private boolean saved;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSaved() {
        return saved;
    }

    public UserAccount() {
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", displayName='" + displayName + '\'' +
                ", saved=" + saved +
                '}';
    }

    public UserAccount(String userName, String password, String role, String displayName, boolean saved) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
        this.saved = saved;
    }
}
