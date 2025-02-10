package com.example.a8_miracle;
import java.io.Serializable;

public class Category implements Serializable {
    private int categoryID;
    private String catName;

    // Default constructor
    public Category() {}

    // Parameterized constructor
    public Category(int categoryID, String catName) {
        this.categoryID = categoryID;
        this.catName = catName;
    }

    // Getter and Setter methods
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}