package com.example.oops_project;

import java.util.ArrayList;

public class category {
    private int id;
    private String name;
    private String shortDes;
    private String imageURL;
    private boolean isExpanded;

    public category(int id, String name, String shortDes, String imageURL) {
        this.id = id;
        this.name = name;
        this.shortDes = shortDes;
        this.imageURL = imageURL;
        isExpanded = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortDes() {
        return shortDes;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String toString() {
        return "category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortDes='" + shortDes + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", isExpanded=" + isExpanded +
                '}';
    }
}
