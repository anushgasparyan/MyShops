package com.example.myshops.model;

public class Rating {
    private String id;
    private String productID;
    private String userID;
    private float rating;
    private String feedback;
    private String id_id;

    public Rating() {
    }

    public Rating(String id, String productID, String userID, float rating, String feedback, String id_id) {
        this.id = id;
        this.productID = productID;
        this.userID = userID;
        this.rating = rating;
        this.feedback = feedback;
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

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getId_id() {
        return id_id;
    }

    public void setId_id(String id_id) {
        this.id_id = id_id;
    }
}
