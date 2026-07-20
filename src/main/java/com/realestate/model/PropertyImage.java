package com.realestate.model;

import java.sql.Timestamp;

/**
 * PropertyImage - Maps to the property_images table.
 * Represents one uploaded image belonging to a property listing.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class PropertyImage {

    private int       imageId;
    private int       propertyId;
    private String    imagePath;
    private String    imageCaption;
    private boolean   isCover;
    private int       displayOrder;
    private Timestamp uploadedAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public PropertyImage() {}

    public PropertyImage(int propertyId, String imagePath,
                         boolean isCover, int displayOrder) {
        this.propertyId   = propertyId;
        this.imagePath    = imagePath;
        this.isCover      = isCover;
        this.displayOrder = displayOrder;
    }

    public PropertyImage(int imageId, int propertyId, String imagePath,
                         String imageCaption, boolean isCover,
                         int displayOrder, Timestamp uploadedAt) {
        this.imageId      = imageId;
        this.propertyId   = propertyId;
        this.imagePath    = imagePath;
        this.imageCaption = imageCaption;
        this.isCover      = isCover;
        this.displayOrder = displayOrder;
        this.uploadedAt   = uploadedAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getImageId()                          { return imageId; }
    public void setImageId(int imageId)              { this.imageId = imageId; }

    public int getPropertyId()                       { return propertyId; }
    public void setPropertyId(int propertyId)        { this.propertyId = propertyId; }

    public String getImagePath()                     { return imagePath; }
    public void setImagePath(String imagePath)       { this.imagePath = imagePath; }

    public String getImageCaption()                  { return imageCaption; }
    public void setImageCaption(String imageCaption) { this.imageCaption = imageCaption; }

    public boolean isCover()                         { return isCover; }
    public void setCover(boolean isCover)            { this.isCover = isCover; }

    public int getDisplayOrder()                     { return displayOrder; }
    public void setDisplayOrder(int displayOrder)    { this.displayOrder = displayOrder; }

    public Timestamp getUploadedAt()                 { return uploadedAt; }
    public void setUploadedAt(Timestamp uploadedAt)  { this.uploadedAt = uploadedAt; }

    @Override
    public String toString() {
        return "PropertyImage{imageId=" + imageId +
               ", propertyId=" + propertyId +
               ", isCover=" + isCover + "}";
    }
}