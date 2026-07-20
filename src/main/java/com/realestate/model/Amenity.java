package com.realestate.model;

import java.sql.Timestamp;

/**
 * Amenity - Maps to the amenities table.
 * Represents a single facility or feature a property may have.
 * Examples: Borehole, Generator, Parking, CCTV, Air Conditioning.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Amenity {

    private int       amenityId;
    private String    amenityName;
    private String    amenityIcon;
    private Timestamp createdAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Amenity() {}

    public Amenity(int amenityId, String amenityName,
                   String amenityIcon, Timestamp createdAt) {
        this.amenityId   = amenityId;
        this.amenityName = amenityName;
        this.amenityIcon = amenityIcon;
        this.createdAt   = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getAmenityId()                        { return amenityId; }
    public void setAmenityId(int amenityId)          { this.amenityId = amenityId; }

    public String getAmenityName()                   { return amenityName; }
    public void setAmenityName(String amenityName)   { this.amenityName = amenityName; }

    public String getAmenityIcon()                   { return amenityIcon; }
    public void setAmenityIcon(String amenityIcon)   { this.amenityIcon = amenityIcon; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Amenity{amenityId=" + amenityId + ", amenityName='" + amenityName + "'}";
    }
}