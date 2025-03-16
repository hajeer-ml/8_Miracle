package com.example.a8_miracle;

public class CartItem {
    private int cartItemId;
    private int bookId;
    private String bookName;
    private double price;
    private int quantity;
    private String imageUrl;

    public CartItem(int cartItemId , int bookId, String bookName, double price, int quantity, String imageUrl) {
        this.cartItemId = cartItemId;
        this.bookId = bookId;
        this.bookName = bookName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
    public int getCartItemId() {
        return cartItemId;
    }

    public int getBookId() { return bookId; }
    public String getBookName() { return bookName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
}
