package com.example.a8_miracle;

public class OrderItem {
    private int bookID;
    private int quantity;
    private double price;
    private String title;
    private String coverImage;

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public OrderItem(int bookID, int quantity, double price, String title, String coverImage) {
        this.bookID = bookID;
        this.quantity = quantity;
        this.price = price;
        this.title = title;
        this.coverImage = coverImage;
    }

    // Getters and Setters
}