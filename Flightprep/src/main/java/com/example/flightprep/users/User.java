package com.example.flightprep.users;

public class User {
    public String user_id;
    protected String password;
    protected String role;

    public User(String user_id, String password, String role) {
        this.user_id = user_id;
        this.password = password;
        this.role = role;
    }
    public String getPassword() {
        return this.password;
    }
}
