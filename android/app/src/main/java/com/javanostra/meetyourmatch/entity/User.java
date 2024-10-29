package com.javanostra.meetyourmatch;

public class User {
    private String username;
    private String interests;
    private String email;
    private int age;
    private int id;
    private static int nextId = 0;


    public User(String username, String interests, String email, int age) {
        this.username = username;
        this.interests = interests;
        this.email = email;
        this.age = age;
        this.id = nextId++;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }
}
