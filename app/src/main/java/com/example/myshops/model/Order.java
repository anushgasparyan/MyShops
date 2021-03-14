package com.example.myshops.model;

public class Order {
    private String id;
    private String productID;
    private String userID;
    private String id_id;

    public Order() {
    }

    public Order(String id, String productID, String userID, String id_id) {
        this.id = id;
        this.productID = productID;
        this.userID = userID;
        this.id_id = id_id;
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

    public String getId_id() {
        return id_id;
    }

    public void setId_id(String id_id) {
        this.id_id = id_id;
    }
}
