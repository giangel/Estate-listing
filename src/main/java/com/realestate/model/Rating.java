package com.realestate.model;

import java.sql.Timestamp;

/**
 * Rating - Maps to the property_ratings table.
 * Represents a numeric star rating (1–5) given by a user to a property.
 * Each user can rate a property only once (enforced by unique constraint).
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Rating {

    private int       ratingId;
    private int       propertyId;
    private int       userId;
    private int       ratingValue;    // 1 to 5
    private Timestamp ratedAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Rating() {}

    public Rating(int propertyId, int userId, int ratingValue) {
        this.propertyId  = propertyId;
        this.userId      = userId;
        this.ratingValue = ratingValue;
    }

    public Rating(int ratingId, int propertyId, int userId,
                  int ratingValue, Timestamp ratedAt) {
        this.ratingId    = ratingId;
        this.propertyId  = propertyId;
        this.userId      = userId;
        this.ratingValue = ratingValue;
        this.ratedAt     = ratedAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getRatingId()                         { return ratingId; }
    public void setRatingId(int ratingId)            { this.ratingId = ratingId; }

    public int getPropertyId()                       { return propertyId; }
    public void setPropertyId(int propertyId)        { this.propertyId = propertyId; }

    public int getUserId()                           { return userId; }
    public void setUserId(int userId)                { this.userId = userId; }

    public int getRatingValue()                      { return ratingValue; }
    public void setRatingValue(int ratingValue)      { this.ratingValue = ratingValue; }

    public Timestamp getRatedAt()                    { return ratedAt; }
    public void setRatedAt(Timestamp ratedAt)        { this.ratedAt = ratedAt; }

    @Override
    public String toString() {
        return "Rating{ratingId=" + ratingId +
               ", propertyId=" + propertyId +
               ", ratingValue=" + ratingValue + "}";
    }
}