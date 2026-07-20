package com.realestate.model;

import java.sql.Timestamp;

/**
 * Landlord - Maps to the landlords table.
 * Holds property owner profile and verification data.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Landlord {

    private int       landlordId;
    private int       userId;
    private String    businessName;
    private String    nin;
    private boolean   isVerified;
    private Timestamp verifiedAt;
    private Timestamp createdAt;

    // Convenience field - populated by JOIN in DAO
    private User user;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Landlord() {}

    public Landlord(int userId, String businessName, String nin) {
        this.userId       = userId;
        this.businessName = businessName;
        this.nin          = nin;
    }

    public Landlord(int landlordId, int userId, String businessName,
                    String nin, boolean isVerified,
                    Timestamp verifiedAt, Timestamp createdAt) {
        this.landlordId   = landlordId;
        this.userId       = userId;
        this.businessName = businessName;
        this.nin          = nin;
        this.isVerified   = isVerified;
        this.verifiedAt   = verifiedAt;
        this.createdAt    = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getLandlordId()                         { return landlordId; }
    public void setLandlordId(int landlordId)          { this.landlordId = landlordId; }

    public int getUserId()                             { return userId; }
    public void setUserId(int userId)                  { this.userId = userId; }

    public String getBusinessName()                    { return businessName; }
    public void setBusinessName(String businessName)   { this.businessName = businessName; }

    public String getNin()                             { return nin; }
    public void setNin(String nin)                     { this.nin = nin; }

    public boolean isVerified()                        { return isVerified; }
    public void setVerified(boolean isVerified)        { this.isVerified = isVerified; }

    public Timestamp getVerifiedAt()                   { return verifiedAt; }
    public void setVerifiedAt(Timestamp verifiedAt)    { this.verifiedAt = verifiedAt; }

    public Timestamp getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)      { this.createdAt = createdAt; }

    public User getUser()                              { return user; }
    public void setUser(User user)                     { this.user = user; }

    @Override
    public String toString() {
        return "Landlord{landlordId=" + landlordId +
               ", businessName='" + businessName + "'" +
               ", isVerified=" + isVerified + "}";
    }
}