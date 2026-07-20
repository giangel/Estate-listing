package com.realestate.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Property - Maps to the properties table.
 * The central entity of the system. Holds all listing details
 * including price, quality indicators, campus distance,
 * verification status, and performance counters.
 *
 * Convenience collections (images, amenities, location) are
 * populated by PropertyDAO when a full property record is needed.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Property {

    // ---------------------------------------------------------------
    // Core Identity
    // ---------------------------------------------------------------
    private int        propertyId;
    private int        ownerId;
    private int        typeId;
    private int        categoryId;

    // ---------------------------------------------------------------
    // Basic Details
    // ---------------------------------------------------------------
    private String     title;
    private String     description;
    private BigDecimal price;
    private int        bedrooms;
    private int        bathrooms;
    private BigDecimal sizeSqm;
    private String     furnishingStatus;   // FURNISHED, SEMI_FURNISHED, UNFURNISHED

    // ---------------------------------------------------------------
    // Campus Intelligence Fields
    // ---------------------------------------------------------------
    private String     campusDistance;     // ON_CAMPUS, LESS_5MIN, 5_TO_10MIN, 10_TO_15MIN, ABOVE_15MIN
    private String     waterAvailability;  // EXCELLENT, GOOD, FAIR, POOR, NONE, UNKNOWN
    private String     electricity;        // 24HRS, PARTIAL, RARE, NONE, UNKNOWN
    private String     securityLevel;      // HIGH, MEDIUM, LOW, UNKNOWN
    private boolean    internetAvailable;
    private String     roadAccessibility;  // TARRED, MOTORABLE, ROUGH, UNKNOWN
    private boolean    isFenced;

    // ---------------------------------------------------------------
    // Status and Flags
    // ---------------------------------------------------------------
    private String     propertyStatus;     // PENDING, AVAILABLE, RESERVED, OCCUPIED, SUSPENDED
    private boolean    isFeatured;
    private boolean    isVerified;
    private String     verificationStatus; // PENDING, VERIFIED, REJECTED

    // ---------------------------------------------------------------
    // Performance Counters
    // ---------------------------------------------------------------
    private int        viewCount;
    private int        saveCount;
    private int        inquiryCount;

    // ---------------------------------------------------------------
    // Images
    // ---------------------------------------------------------------
    private String     coverImage;

    // ---------------------------------------------------------------
    // Approval
    // ---------------------------------------------------------------
    private int        approvedBy;
    private Timestamp  approvedAt;

    // ---------------------------------------------------------------
    // Timestamps
    // ---------------------------------------------------------------
    private Timestamp  createdAt;
    private Timestamp  updatedAt;

    // ---------------------------------------------------------------
    // Joined / Convenience Fields (populated by DAO via JOIN)
    // ---------------------------------------------------------------
    private String             typeName;
    private String             categoryName;
    private String             ownerName;
    private String             ownerEmail;
    private String             ownerPhone;
    private String             ownerPhoto;
    private double             averageRating;
    private int                totalRatings;
    private PropertyLocation   location;
    private List<PropertyImage> images;
    private List<Amenity>       amenities;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Property() {}

    /**
     * Constructor for creating a new property listing.
     */
    public Property(int ownerId, int typeId, int categoryId,
                    String title, String description, BigDecimal price,
                    int bedrooms, int bathrooms, String campusDistance) {
        this.ownerId        = ownerId;
        this.typeId         = typeId;
        this.categoryId     = categoryId;
        this.title          = title;
        this.description    = description;
        this.price          = price;
        this.bedrooms       = bedrooms;
        this.bathrooms      = bathrooms;
        this.campusDistance = campusDistance;
    }

    // ---------------------------------------------------------------
    // Convenience / Display Methods
    // ---------------------------------------------------------------

    /**
     * Returns a human-readable campus distance label for display.
     */
    public String getCampusDistanceLabel() {
        if (campusDistance == null) return "Unknown";
        switch (campusDistance) {
            case "ON_CAMPUS":   return "On Campus";
            case "LESS_5MIN":   return "Less than 5 Minutes";
            case "5_TO_10MIN":  return "5 – 10 Minutes";
            case "10_TO_15MIN": return "10 – 15 Minutes";
            case "ABOVE_15MIN": return "More than 15 Minutes";
            default:            return campusDistance;
        }
    }

    /**
     * Returns a Bootstrap Icon class appropriate for the campus distance.
     */
    public String getCampusDistanceIcon() {
        if (campusDistance == null) return "bi-geo-alt";
        switch (campusDistance) {
            case "ON_CAMPUS":   return "bi-building-fill";
            case "LESS_5MIN":   return "bi-person-walking";
            case "5_TO_10MIN":  return "bi-person-walking";
            case "10_TO_15MIN": return "bi-bicycle";
            case "ABOVE_15MIN": return "bi-car-front";
            default:            return "bi-geo-alt";
        }
    }

    /**
     * Returns a CSS badge class for the property status.
     */
    public String getStatusBadgeClass() {
        if (propertyStatus == null) return "re-badge-pending";
        switch (propertyStatus) {
            case "AVAILABLE": return "re-badge-available";
            case "RESERVED":  return "re-badge-reserved";
            case "OCCUPIED":  return "re-badge-occupied";
            case "SUSPENDED": return "re-badge-occupied";
            default:          return "re-badge-pending";
        }
    }

    /**
     * Returns a CSS quality class for the water availability level.
     */
    public String getWaterQualityClass() {
        return getQualityClass(waterAvailability);
    }

    /**
     * Returns a CSS quality class for the electricity level.
     */
    public String getElectricityQualityClass() {
        if (electricity == null) return "quality-unknown";
        switch (electricity) {
            case "24HRS":   return "quality-excellent";
            case "PARTIAL": return "quality-good";
            case "RARE":    return "quality-poor";
            case "NONE":    return "quality-none";
            default:        return "quality-unknown";
        }
    }

    /**
     * Returns a CSS quality class for the security level.
     */
    public String getSecurityQualityClass() {
        if (securityLevel == null) return "quality-unknown";
        switch (securityLevel) {
            case "HIGH":   return "quality-excellent";
            case "MEDIUM": return "quality-fair";
            case "LOW":    return "quality-poor";
            default:       return "quality-unknown";
        }
    }

    /**
     * Returns a CSS quality class for road accessibility.
     */
    public String getRoadQualityClass() {
        if (roadAccessibility == null) return "quality-unknown";
        switch (roadAccessibility) {
            case "TARRED":    return "quality-excellent";
            case "MOTORABLE": return "quality-fair";
            case "ROUGH":     return "quality-poor";
            default:          return "quality-unknown";
        }
    }

    /**
     * Generic quality class mapper for EXCELLENT/GOOD/FAIR/POOR/NONE.
     */
    private String getQualityClass(String level) {
        if (level == null) return "quality-unknown";
        switch (level) {
            case "EXCELLENT": return "quality-excellent";
            case "GOOD":      return "quality-good";
            case "FAIR":      return "quality-fair";
            case "POOR":      return "quality-poor";
            case "NONE":      return "quality-none";
            default:          return "quality-unknown";
        }
    }

    /**
     * Returns a formatted Naira price string.
     * Example: 85000.00 → "₦85,000"
     */
    public String getFormattedPrice() {
        if (price == null) return "₦0";
        return "₦" + String.format("%,.0f", price);
    }

    /**
     * Returns the cover image path or a placeholder if none set.
     */
    public String getCoverImageOrPlaceholder() {
        if (coverImage != null && !coverImage.isEmpty()) return coverImage;
        return "assets/images/placeholder.jpg";
    }

    /**
     * Returns a student budget category label for this property's price.
     */
    public String getBudgetCategory() {
        if (price == null) return "Unknown";
        double p = price.doubleValue();
        if (p < 50000)              return "Under ₦50,000";
        if (p <= 100000)            return "₦50,000 – ₦100,000";
        if (p <= 150000)            return "₦100,000 – ₦150,000";
        if (p <= 200000)            return "₦150,000 – ₦200,000";
        return "Above ₦200,000";
    }

    /**
     * Returns a popularity score used for sorting.
     * Weighted: view × 1, save × 3, inquiry × 5
     */
    public int getPopularityScore() {
        return (viewCount * 1) + (saveCount * 3) + (inquiryCount * 5);
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getPropertyId()                           { return propertyId; }
    public void setPropertyId(int propertyId)            { this.propertyId = propertyId; }

    public int getOwnerId()                              { return ownerId; }
    public void setOwnerId(int ownerId)                  { this.ownerId = ownerId; }

    public int getTypeId()                               { return typeId; }
    public void setTypeId(int typeId)                    { this.typeId = typeId; }

    public int getCategoryId()                           { return categoryId; }
    public void setCategoryId(int categoryId)            { this.categoryId = categoryId; }

    public String getTitle()                             { return title; }
    public void setTitle(String title)                   { this.title = title; }

    public String getDescription()                       { return description; }
    public void setDescription(String description)       { this.description = description; }

    public BigDecimal getPrice()                         { return price; }
    public void setPrice(BigDecimal price)               { this.price = price; }

    public int getBedrooms()                             { return bedrooms; }
    public void setBedrooms(int bedrooms)                { this.bedrooms = bedrooms; }

    public int getBathrooms()                            { return bathrooms; }
    public void setBathrooms(int bathrooms)              { this.bathrooms = bathrooms; }

    public BigDecimal getSizeSqm()                       { return sizeSqm; }
    public void setSizeSqm(BigDecimal sizeSqm)           { this.sizeSqm = sizeSqm; }

    public String getFurnishingStatus()                              { return furnishingStatus; }
    public void setFurnishingStatus(String furnishingStatus)         { this.furnishingStatus = furnishingStatus; }

    public String getCampusDistance()                                { return campusDistance; }
    public void setCampusDistance(String campusDistance)             { this.campusDistance = campusDistance; }

    public String getWaterAvailability()                             { return waterAvailability; }
    public void setWaterAvailability(String waterAvailability)       { this.waterAvailability = waterAvailability; }

    public String getElectricity()                                   { return electricity; }
    public void setElectricity(String electricity)                   { this.electricity = electricity; }

    public String getSecurityLevel()                                 { return securityLevel; }
    public void setSecurityLevel(String securityLevel)               { this.securityLevel = securityLevel; }

    public boolean isInternetAvailable()                             { return internetAvailable; }
    public void setInternetAvailable(boolean internetAvailable)      { this.internetAvailable = internetAvailable; }

    public String getRoadAccessibility()                             { return roadAccessibility; }
    public void setRoadAccessibility(String roadAccessibility)       { this.roadAccessibility = roadAccessibility; }

    public boolean isFenced()                                        { return isFenced; }
    public void setFenced(boolean isFenced)                          { this.isFenced = isFenced; }

    public String getPropertyStatus()                                { return propertyStatus; }
    public void setPropertyStatus(String propertyStatus)             { this.propertyStatus = propertyStatus; }

    public boolean isFeatured()                                      { return isFeatured; }
    public void setFeatured(boolean isFeatured)                      { this.isFeatured = isFeatured; }

    public boolean isVerified()                                      { return isVerified; }
    public void setVerified(boolean isVerified)                      { this.isVerified = isVerified; }

    public String getVerificationStatus()                            { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus)     { this.verificationStatus = verificationStatus; }

    public int getViewCount()                                        { return viewCount; }
    public void setViewCount(int viewCount)                          { this.viewCount = viewCount; }

    public int getSaveCount()                                        { return saveCount; }
    public void setSaveCount(int saveCount)                          { this.saveCount = saveCount; }

    public int getInquiryCount()                                     { return inquiryCount; }
    public void setInquiryCount(int inquiryCount)                    { this.inquiryCount = inquiryCount; }

    public String getCoverImage()                                    { return coverImage; }
    public void setCoverImage(String coverImage)                     { this.coverImage = coverImage; }

    public int getApprovedBy()                                       { return approvedBy; }
    public void setApprovedBy(int approvedBy)                        { this.approvedBy = approvedBy; }

    public Timestamp getApprovedAt()                                 { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt)                  { this.approvedAt = approvedAt; }

    public Timestamp getCreatedAt()                                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)                    { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()                                  { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt)                    { this.updatedAt = updatedAt; }

    // Joined fields
    public String getTypeName()                          { return typeName; }
    public void setTypeName(String typeName)             { this.typeName = typeName; }

    public String getCategoryName()                      { return categoryName; }
    public void setCategoryName(String categoryName)     { this.categoryName = categoryName; }

    public String getOwnerName()                         { return ownerName; }
    public void setOwnerName(String ownerName)           { this.ownerName = ownerName; }

    public String getOwnerEmail()                        { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail)         { this.ownerEmail = ownerEmail; }

    public String getOwnerPhone()                        { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone)         { this.ownerPhone = ownerPhone; }

    public String getOwnerPhoto()                        { return ownerPhoto; }
    public void setOwnerPhoto(String ownerPhoto)         { this.ownerPhoto = ownerPhoto; }

    public double getAverageRating()                     { return averageRating; }
    public void setAverageRating(double averageRating)   { this.averageRating = averageRating; }

    public int getTotalRatings()                         { return totalRatings; }
    public void setTotalRatings(int totalRatings)        { this.totalRatings = totalRatings; }

    public PropertyLocation getLocation()                         { return location; }
    public void setLocation(PropertyLocation location)            { this.location = location; }

    public List<PropertyImage> getImages()                        { return images; }
    public void setImages(List<PropertyImage> images)             { this.images = images; }

    public List<Amenity> getAmenities()                           { return amenities; }
    public void setAmenities(List<Amenity> amenities)             { this.amenities = amenities; }

    // ---------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Property{propertyId=" + propertyId +
               ", title='" + title + "'" +
               ", price=" + price +
               ", campusDistance='" + campusDistance + "'" +
               ", propertyStatus='" + propertyStatus + "'" +
               ", isVerified=" + isVerified + "}";
    }
}