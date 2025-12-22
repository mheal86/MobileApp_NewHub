package com.example.mobileapp_newhub.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid;
    private String name;
    private String email;
    private String photoUrl;
    private String role; // "user" or "admin"
    private List<String> interests;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.interests = new ArrayList<>();
    }

    public User(String uid, String name, String email, String photoUrl, String role) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.role = role;
        this.interests = new ArrayList<>();
    }

    public User(String uid, String name, String email, String photoUrl, String role, List<String> interests) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.role = role;
        this.interests = interests != null ? interests : new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}