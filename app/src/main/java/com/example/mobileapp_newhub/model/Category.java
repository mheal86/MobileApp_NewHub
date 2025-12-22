package com.example.mobileapp_newhub.model;

import java.io.Serializable;

public class Category implements Serializable {
    public String id;
    public String name;
    public String imageUrl;

    public Category() {}
    
    public Category(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
