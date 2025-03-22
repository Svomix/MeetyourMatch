package com.javanostra.meetyourmatch.persistance.entity;

public class NewUserDTO {
    private String username;
    private String email;
    private String password;

    public NewUserDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
