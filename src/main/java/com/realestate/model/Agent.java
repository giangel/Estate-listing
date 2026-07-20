package com.realestate.model;

import java.sql.Timestamp;

/**
 * Agent - Maps to the agents table.
 * Holds real estate agent profile and verification status.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Agent {

    private int       agentId;
    private int       userId;
    private String    agencyName;
    private String    licenseNumber;
    private String    verificationStatus;  // PENDING, VERIFIED, REJECTED
    private Timestamp verifiedAt;
    private int       verifiedBy;
    private Timestamp createdAt;

    // Convenience fields
    private User   user;
    private String verifiedByName;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Agent() {}

    public Agent(int userId, String agencyName, String licenseNumber) {
        this.userId        = userId;
        this.agencyName    = agencyName;
        this.licenseNumber = licenseNumber;
    }

    public Agent(int agentId, int userId, String agencyName, String licenseNumber,
                 String verificationStatus, Timestamp verifiedAt,
                 int verifiedBy, Timestamp createdAt) {
        this.agentId            = agentId;
        this.userId             = userId;
        this.agencyName         = agencyName;
        this.licenseNumber      = licenseNumber;
        this.verificationStatus = verificationStatus;
        this.verifiedAt         = verifiedAt;
        this.verifiedBy         = verifiedBy;
        this.createdAt          = createdAt;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public boolean isPendingVerification() { return "PENDING".equals(verificationStatus); }
    public boolean isVerified()            { return "VERIFIED".equals(verificationStatus); }
    public boolean isRejected()            { return "REJECTED".equals(verificationStatus); }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getAgentId()                                  { return agentId; }
    public void setAgentId(int agentId)                      { this.agentId = agentId; }

    public int getUserId()                                   { return userId; }
    public void setUserId(int userId)                        { this.userId = userId; }

    public String getAgencyName()                            { return agencyName; }
    public void setAgencyName(String agencyName)             { this.agencyName = agencyName; }

    public String getLicenseNumber()                         { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber)       { this.licenseNumber = licenseNumber; }

    public String getVerificationStatus()                              { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus)       { this.verificationStatus = verificationStatus; }

    public Timestamp getVerifiedAt()                         { return verifiedAt; }
    public void setVerifiedAt(Timestamp verifiedAt)          { this.verifiedAt = verifiedAt; }

    public int getVerifiedBy()                               { return verifiedBy; }
    public void setVerifiedBy(int verifiedBy)                { this.verifiedBy = verifiedBy; }

    public Timestamp getCreatedAt()                          { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)            { this.createdAt = createdAt; }

    public User getUser()                                    { return user; }
    public void setUser(User user)                           { this.user = user; }

    public String getVerifiedByName()                        { return verifiedByName; }
    public void setVerifiedByName(String verifiedByName)     { this.verifiedByName = verifiedByName; }

    @Override
    public String toString() {
        return "Agent{agentId=" + agentId +
               ", agencyName='" + agencyName + "'" +
               ", verificationStatus='" + verificationStatus + "'}";
    }
}