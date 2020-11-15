package com.example.oops_project;

import java.util.ArrayList;

public class category {
    private int id;
    private String name;
    private String shortDes;
    private String imageURL;
    private boolean isExpanded;
    private ArrayList<item> items;

    public category(int id, String name, String shortDes, String imageURL, ArrayList<item> items) {
        this.id = id;
        this.name = name;
        this.shortDes = shortDes;
        this.imageURL = imageURL;
        isExpanded = false;
        this.items = items;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setItems(ArrayList<item> items) {
        this.items = items;
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

    public ArrayList<item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        String s = "[" +
                "\n" + id +
                "\n" + name +
                "\n" + shortDes +
                "\n" + imageURL +
                "\n" + items.size();
        for(item e : items)
            s = s + e.toString();
        s = s + "\n]\n";
        return s;
    }
}
