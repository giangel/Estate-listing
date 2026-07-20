package com.realestate.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * RoommateProfile - Maps to the roommate_profiles table.
 * Holds a student's roommate-seeking preferences used by
 * the roommate matching engine.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class RoommateProfile {

    private int        roommateProfileId;
    private int        userId;
    private String     genderPreference;  // MALE, FEMALE, ANY
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String     preferredArea;
    private String     department;
    private String     level;
    private String     description;
    private boolean    isActive;
    private Timestamp  createdAt;
    private Timestamp  updatedAt;

    // Convenience fields
    private User       user;
    private int        matchScore;        // Populated by matching engine
    private String     userName;          // ADDED: For joined user query details
    private String     profilePhoto;      // ADDED: For joined user query details

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public RoommateProfile() {}

    public RoommateProfile(int userId, String genderPreference,
                           BigDecimal budgetMin, BigDecimal budgetMax,
                           String preferredArea, String department,
                           String level, String description) {
        this.userId        = userId;
        this.genderPreference  = genderPreference;
        this.budgetMin         = budgetMin;
        this.budgetMax         = budgetMax;
        this.preferredArea     = preferredArea;
        this.department        = department;
        this.level             = level;
        this.description       = description;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public String getBudgetRangeDisplay() {
        if (budgetMin == null && budgetMax == null) return "Any Budget";
        if (budgetMin == null) return "Up to ₦" + String.format("%,.0f", budgetMax);
        if (budgetMax == null) return "From ₦" + String.format("%,.0f", budgetMin);
        return "₦" + String.format("%,.0f", budgetMin) +
               " – ₦" + String.format("%,.0f", budgetMax);
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getRoommateProfileId()                               { return roommateProfileId; }
    public void setRoommateProfileId(int roommateProfileId)        { this.roommateProfileId = roommateProfileId; }

    public int getUserId()                                         { return userId; }
    public void setUserId(int userId)                              { this.userId = userId; }

    public String getGenderPreference()                            { return genderPreference; }
    public void setGenderPreference(String genderPreference)       { this.genderPreference = genderPreference; }

    public BigDecimal getBudgetMin()                               { return budgetMin; }
    public void setBudgetMin(BigDecimal budgetMin)                 { this.budgetMin = budgetMin; }

    public BigDecimal getBudgetMax()                               { return budgetMax; }
    public void setBudgetMax(BigDecimal budgetMax)                 { this.budgetMax = budgetMax; }

    public String getPreferredArea()                               { return preferredArea; }
    public void setPreferredArea(String preferredArea)             { this.preferredArea = preferredArea; }

    public String getDepartment()                                  { return department; }
    public void setDepartment(String department)                   { this.department = department; }

    public String getLevel()                                       { return level; }
    public void setLevel(String level)                             { this.level = level; }

    public String getDescription()                                 { return description; }
    public void setDescription(String description)                 { this.description = description; }

    public boolean isActive()                                      { return isActive; }
    public void setActive(boolean isActive)                         { this.isActive = isActive; }

    public Timestamp getCreatedAt()                                { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)                  { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()                                { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt)                  { this.updatedAt = updatedAt; }

    public User getUser()                                          { return user; }
    public void setUser(User user)                                 { this.user = user; }

    public int getMatchScore()                                     { return matchScore; }
    public void setMatchScore(int matchScore)                      { this.matchScore = matchScore; }

    // ADDED: Getters and Setters for joined fields
    public String getUserName()                                    { return userName; }
    public void setUserName(String userName)                      { this.userName = userName; }

    public String getProfilePhoto()                                { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto)              { this.profilePhoto = profilePhoto; }

    @Override
    public String toString() {
        return "RoommateProfile{roommateProfileId=" + roommateProfileId +
               ", userId=" + userId +
               ", userName='" + userName + "'" +
               ", genderPreference='" + genderPreference + "'" +
               ", level='" + level + "'}";
    }
}