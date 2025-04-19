package com.example.a8_miracle;

import java.util.List;

public class Order {
    private int orderID;
    private double totalPrice;
    private String orderDate;
    private String status;
    private String trackingNumber;
    private double deliveryCost;
    private String userName;
    private String phoneNumber;
    private String willaya;
    private String municipality;
    private String deliveryType;
    private List<OrderItem> items;

    public int getOrderID() {
        return orderID;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWillaya() {
        return willaya;
    }

    public String getMunicipality() {
        return municipality;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWillaya(String willaya) {
        this.willaya = willaya;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Order(int orderID, double totalPrice, String orderDate, String status, String trackingNumber, double deliveryCost, String userName, String phoneNumber, String willaya, String municipality, String deliveryType, List<OrderItem> items) {
        this.orderID = orderID;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
        this.trackingNumber = trackingNumber;
        this.deliveryCost = deliveryCost;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.willaya = willaya;
        this.municipality = municipality;
        this.deliveryType = deliveryType;
        this.items = items;
    }

    // Getters and Setters
}