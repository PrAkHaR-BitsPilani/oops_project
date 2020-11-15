package com.example.oops_project;

public class item {
    private String id;
    private String name;
    private int price;
    private int quantity;
    private String imgURI;

    public item(String id, String name, int price, int quantity, String imgURI) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imgURI = imgURI;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImgURI(String imgURI) {
        this.imgURI = imgURI;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImgURI() {
        return imgURI;
    }

    @Override
    public String toString()
    {
        String s = "\n" + id + "\n" + name + "\n" + price + "\n" + quantity + "\n" + imgURI;
        return s;
    }
}
