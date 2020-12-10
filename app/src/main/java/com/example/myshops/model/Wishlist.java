package com.example.myshops.model;

public class Wishlist {
    private String id;
    private String productID;
    private String userID;

    public Wishlist() {
    }

    public Wishlist(String id, String productID, String userID) {
        this.id = id;
        this.productID = productID;
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
