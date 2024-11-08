package com.example.yoga_app.model;

import java.io.Serializable;

public class Role implements Serializable {

    int id;
    String role_name;

    public Role(){

    }
    public Role(int id, String role_name) {
        this.id = id;
        this.role_name = role_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
