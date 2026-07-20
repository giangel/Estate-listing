package com.realestate.model;

import java.sql.Timestamp;

/**
 * PropertyLocation - Maps to the property_locations table.
 * One-to-one with Property. Stores address and area information
 * for display on the property detail page and search results.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class PropertyLocation {

    private int       locationId;
    private int       propertyId;
    private String    areaName;
    private String    streetAddress;
    private String    landmark;
    private String    lga;
    private String    state;
    private Timestamp createdAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public PropertyLocation() {}

    public PropertyLocation(int propertyId, String areaName,
                             String streetAddress, String landmark,
                             String lga, String state) {
        this.propertyId    = propertyId;
        this.areaName      = areaName;
        this.streetAddress = streetAddress;
        this.landmark      = landmark;
        this.lga           = lga;
        this.state         = state;
    }

    public PropertyLocation(int locationId, int propertyId, String areaName,
                             String streetAddress, String landmark,
                             String lga, String state, Timestamp createdAt) {
        this.locationId    = locationId;
        this.propertyId    = propertyId;
        this.areaName      = areaName;
        this.streetAddress = streetAddress;
        this.landmark      = landmark;
        this.lga           = lga;
        this.state         = state;
        this.createdAt     = createdAt;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    /**
     * Returns a one-line display address for property cards.
     * Example: "Eruwa Town Centre, Ibarapa East"
     */
    public String getDisplayAddress() {
        if (areaName != null && lga != null) return areaName + ", " + lga;
        if (areaName != null)                return areaName;
        return "Eruwa, Oyo State";
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getLocationId()                         { return locationId; }
    public void setLocationId(int locationId)          { this.locationId = locationId; }

    public int getPropertyId()                         { return propertyId; }
    public void setPropertyId(int propertyId)          { this.propertyId = propertyId; }

    public String getAreaName()                        { return areaName; }
    public void setAreaName(String areaName)           { this.areaName = areaName; }

    public String getStreetAddress()                   { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getLandmark()                        { return landmark; }
    public void setLandmark(String landmark)           { this.landmark = landmark; }

    public String getLga()                             { return lga; }
    public void setLga(String lga)                     { this.lga = lga; }

    public String getState()                           { return state; }
    public void setState(String state)                 { this.state = state; }

    public Timestamp getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)      { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "PropertyLocation{locationId=" + locationId +
               ", areaName='" + areaName + "'" +
               ", lga='" + lga + "'}";
    }
}