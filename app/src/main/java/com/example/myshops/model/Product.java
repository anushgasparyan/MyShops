package com.example.myshops.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class Product {
    private String id;
    private String name;
    private String desc;
    private String userID;
    private Category category;
    private Currency currency;
    private double price;
    private int count;
    private int active;
    private String date;
    private List<String> productImgs;

    public Product() {
    }

    public Product(String id, String name, String desc, String userID, Category category, Currency currency, double price, int count, int active, String date, List<String> productImgs) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.userID = userID;
        this.category = category;
        this.currency = currency;
        this.price = price;
        this.count = count;
        this.active = active;
        this.date = date;
        this.productImgs = productImgs;
    }

    public List<String> getProductImgs() {
        return productImgs;
    }

    public void setProductImgs(List<String> productImgs) {
        this.productImgs = productImgs;
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", userID='" + userID + '\'' +
                ", category=" + category +
                ", currency=" + currency +
                ", price=" + price +
                ", count=" + count +
                ", active=" + active +
                ", date='" + date + '\'' +
                ", productImgs=" + productImgs +
                '}';
    }
}
