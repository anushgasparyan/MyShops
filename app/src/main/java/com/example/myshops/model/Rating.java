package com.example.myshops.model;

public class Rating {
    private String id;
    private String productID;
    private String userID;
    private double rating;
    private String feedback;

    public Rating() {
    }

    public Rating(String id, String productID, String userID, double rating, String feedback) {
        this.id = id;
        this.productID = productID;
        this.userID = userID;
        this.rating = rating;
        this.feedback = feedback;
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

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
