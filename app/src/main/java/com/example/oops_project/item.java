package com.example.oops_project;

public class item {
    private String id;
    private String name;
    private String price;
    private String quantity;
    private String imgURI;

    public item(String id, String name, String price, String quantity, String imgURI) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imgURI = imgURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImgURI() {
        return imgURI;
    }

    public void setImgURI(String imgURI) {
        this.imgURI = imgURI;
    }

    @Override
    public String toString() {
        String s = "\n" + id + "\n" + name + "\n" + price + "\n" + quantity + "\n" + imgURI;
        return s;
    }
}
