package com.realestate.model;

import java.sql.Timestamp;

/**
 * Review - Maps to the property_reviews table.
 * Represents a written review submitted by a user for a property.
 * Reviews are moderated: status moves from PENDING → APPROVED or REJECTED.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Review {

    private int       reviewId;
    private int       propertyId;
    private int       userId;
    private String    reviewTitle;
    private String    reviewBody;
    private String    reviewStatus;   // PENDING, APPROVED, REJECTED
    private Timestamp createdAt;

    // Convenience fields
    private String    reviewerName;
    private String    reviewerPhoto;
    private int       ratingValue;    // The associated rating (1–5)

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Review() {}

    public Review(int propertyId, int userId,
                  String reviewTitle, String reviewBody) {
        this.propertyId  = propertyId;
        this.userId      = userId;
        this.reviewTitle = reviewTitle;
        this.reviewBody  = reviewBody;
    }

    public Review(int reviewId, int propertyId, int userId,
                  String reviewTitle, String reviewBody,
                  String reviewStatus, Timestamp createdAt) {
        this.reviewId     = reviewId;
        this.propertyId   = propertyId;
        this.userId       = userId;
        this.reviewTitle  = reviewTitle;
        this.reviewBody   = reviewBody;
        this.reviewStatus = reviewStatus;
        this.createdAt    = createdAt;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public boolean isPending()  { return "PENDING".equals(reviewStatus); }
    public boolean isApproved() { return "APPROVED".equals(reviewStatus); }
    public boolean isRejected() { return "REJECTED".equals(reviewStatus); }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getReviewId()                           { return reviewId; }
    public void setReviewId(int reviewId)              { this.reviewId = reviewId; }

    public int getPropertyId()                         { return propertyId; }
    public void setPropertyId(int propertyId)          { this.propertyId = propertyId; }

    public int getUserId()                             { return userId; }
    public void setUserId(int userId)                  { this.userId = userId; }

    public String getReviewTitle()                     { return reviewTitle; }
    public void setReviewTitle(String reviewTitle)     { this.reviewTitle = reviewTitle; }

    public String getReviewBody()                      { return reviewBody; }
    public void setReviewBody(String reviewBody)       { this.reviewBody = reviewBody; }

    public String getReviewStatus()                    { return reviewStatus; }
    public void setReviewStatus(String reviewStatus)   { this.reviewStatus = reviewStatus; }

    public Timestamp getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)      { this.createdAt = createdAt; }

    public String getReviewerName()                    { return reviewerName; }
    public void setReviewerName(String reviewerName)   { this.reviewerName = reviewerName; }

    public String getReviewerPhoto()                   { return reviewerPhoto; }
    public void setReviewerPhoto(String reviewerPhoto) { this.reviewerPhoto = reviewerPhoto; }

    public int getRatingValue()                        { return ratingValue; }
    public void setRatingValue(int ratingValue)        { this.ratingValue = ratingValue; }

    @Override
    public String toString() {
        return "Review{reviewId=" + reviewId +
               ", propertyId=" + propertyId +
               ", reviewTitle='" + reviewTitle + "'" +
               ", status='" + reviewStatus + "'}";
    }
}