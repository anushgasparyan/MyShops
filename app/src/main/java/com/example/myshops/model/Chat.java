package com.example.myshops.model;

public class Chat {
    private String id;
    private String fromID;
    private String toID;
    private String message;

    public Chat() {
    }

    public Chat(String id, String fromID, String toID, String message) {
        this.id = id;
        this.fromID = fromID;
        this.toID = toID;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
