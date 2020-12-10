package com.example.myshops.model;

public class ProductImg {
    private String id;
    private String productID;
    private String url;

    public ProductImg() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ProductImg(String id, String productID, String url) {

        this.id = id;
        this.productID = productID;
        this.url = url;
    }
}
