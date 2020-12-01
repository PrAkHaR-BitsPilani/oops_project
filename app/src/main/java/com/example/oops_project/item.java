package com.example.oops_project;

public class item {
    private String id;
    private String name;
    private String price;
    private String quantity;
    private String imgURI;
    private String shareURI;
    private int categoryId;
    private boolean isExpanded;

    public item(String id, int categoryId, String name, String price, String quantity, String imgURI, String shareURI) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imgURI = imgURI;
        this.shareURI = shareURI;
        this.isExpanded = false;
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

    public String getShareURI() {
        return shareURI;
    }

    public void setShareURI(String shareURI) {
        this.shareURI = shareURI;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public String toString() {
        String s = "\n" + id + "\n" + categoryId + "\n" + name + "\n" + price + "\n" + quantity + "\n" + imgURI + "\n" + shareURI;
        return s;
    }
}
