package com.mcafi.calcetto.model;

public class User {
    private String email;
    private String name;
    private String username;

    public User() {

    }

    public User(String email, String name, String username) {
        this.email = email;
        this.name = name;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
