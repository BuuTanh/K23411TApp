package com.tranlebuutanh.models;

import java.io.Serializable;

public class FirebaseContact implements Serializable {
    public static final String SYNCED = "SYNCED";
    public static final String PENDING = "PENDING";

    private String id;
    private String name;
    private String phone;
    private String email;
    private String syncStatus;

    public FirebaseContact() {
    }

    public FirebaseContact(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.syncStatus = SYNCED;
    }

    public FirebaseContact(String id, String name, String phone, String email, String syncStatus) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.syncStatus = syncStatus;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + phone + " | " + email;
    }
}
