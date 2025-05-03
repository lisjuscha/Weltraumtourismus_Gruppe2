package com.example.flightprep.model;

public class User {
    private String userId;
    private String password;
    private String role;

    public User(String user_id, String password, String role) {
        this.userId = user_id;
        this.password = password;
        this.role = role;
    }


    public String getId() {
        return this.userId;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role;
    }

}
