package com.realestate.model;

import java.sql.Timestamp;

/**
 * PropertyCategory - Maps to the property_categories table.
 * Examples: Student Accommodation, Family Housing, Shared Accommodation.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class PropertyCategory {

    private int       categoryId;
    private String    categoryName;
    private Timestamp createdAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public PropertyCategory() {}

    public PropertyCategory(int categoryId, String categoryName, Timestamp createdAt) {
        this.categoryId   = categoryId;
        this.categoryName = categoryName;
        this.createdAt    = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getCategoryId()                         { return categoryId; }
    public void setCategoryId(int categoryId)          { this.categoryId = categoryId; }

    public String getCategoryName()                    { return categoryName; }
    public void setCategoryName(String categoryName)   { this.categoryName = categoryName; }

    public Timestamp getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)      { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "PropertyCategory{categoryId=" + categoryId +
               ", categoryName='" + categoryName + "'}";
    }
}