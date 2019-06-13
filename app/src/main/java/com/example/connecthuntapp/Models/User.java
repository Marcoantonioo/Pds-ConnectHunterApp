package com.example.connecthuntapp.Models;

public class User {
    private String name;
    private String typeUser;

    private User(){}

    public User(String name, String typeUser) {
        this.name = name;
        this.typeUser = typeUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }
}
