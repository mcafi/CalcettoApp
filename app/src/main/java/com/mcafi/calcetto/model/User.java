package com.mcafi.calcetto.model;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("email", this.email);
        map.put("name", this.name);
        map.put("username", this.username);
        return map;
    }
}
