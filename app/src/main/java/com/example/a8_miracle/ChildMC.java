package com.example.a8_miracle;

import java.io.Serializable;

public class ChildMC implements Serializable {
    private int bookID;
    private String title;
    private String author;
    private double price;
    private float rating;
    private int stockQuantity;
    private String description;
    private String coverImage;
    private String publishedDate;
    private int categoryID;

    public ChildMC(int bookID, String title, String author, double price, float rating, int stockQuantity, String description, String coverImage, String publishedDate, int categoryID) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.price = price;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.coverImage = coverImage;
        this.publishedDate = publishedDate;
        this.categoryID = categoryID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getPrice() {
        return String.valueOf(price);
    }

    public String getTitle() {
        return title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}