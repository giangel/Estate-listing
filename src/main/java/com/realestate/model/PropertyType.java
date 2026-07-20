package com.realestate.model;

import java.sql.Timestamp;

/**
 * PropertyType - Maps to the property_types table.
 * Examples: Self-Contain, Single Room, Mini Flat, Duplex, Hostel.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class PropertyType {

    private int       typeId;
    private String    typeName;
    private String    typeIcon;
    private Timestamp createdAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public PropertyType() {}

    public PropertyType(int typeId, String typeName,
                        String typeIcon, Timestamp createdAt) {
        this.typeId    = typeId;
        this.typeName  = typeName;
        this.typeIcon  = typeIcon;
        this.createdAt = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getTypeId()                     { return typeId; }
    public void setTypeId(int typeId)          { this.typeId = typeId; }

    public String getTypeName()                { return typeName; }
    public void setTypeName(String typeName)   { this.typeName = typeName; }

    public String getTypeIcon()                { return typeIcon; }
    public void setTypeIcon(String typeIcon)   { this.typeIcon = typeIcon; }

    public Timestamp getCreatedAt()            { return createdAt; }
    public void setCreatedAt(Timestamp t)      { this.createdAt = t; }

    @Override
    public String toString() {
        return "PropertyType{typeId=" + typeId + ", typeName='" + typeName + "'}";
    }
}