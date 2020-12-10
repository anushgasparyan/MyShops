package com.example.myshops.model;

public class Card {
    private String id;
    private String productID;
    private String userID;
    private int quantity;

    public Card() {
    }

    public Card(String id, String productID, String userID, int quantity) {
        this.id = id;
        this.productID = productID;
        this.userID = userID;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
