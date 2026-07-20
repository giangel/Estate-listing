package com.realestate.model;

import java.sql.Timestamp;

/**
 * User - Maps to the users table.
 * Central entity for all authenticated users regardless of role.
 * Contains authentication fields and account status.
 * Role-specific details are held in Student, Staff, Landlord, or Agent.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class User {

    private int       userId;
    private int       roleId;
    private String    roleName;       // Joined from roles table for convenience
    private String    fullName;
    private String    email;
    private String    passwordHash;
    private String    phone;
    private String    profilePhoto;
    private String    accountStatus;  // ACTIVE, SUSPENDED, BLACKLISTED, PENDING
    private boolean   isVerified;
    private String    resetToken;
    private Timestamp resetTokenExpiry;
    private Timestamp lastLogin;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public User() {}

    /**
     * Minimal constructor used for registration.
     */
    public User(int roleId, String fullName, String email,
                String passwordHash, String phone) {
        this.roleId       = roleId;
        this.fullName     = fullName;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.phone        = phone;
    }

    /**
     * Full constructor used when reading a complete record from the database.
     */
    public User(int userId, int roleId, String roleName, String fullName,
                String email, String passwordHash, String phone,
                String profilePhoto, String accountStatus, boolean isVerified,
                String resetToken, Timestamp resetTokenExpiry,
                Timestamp lastLogin, Timestamp createdAt, Timestamp updatedAt) {
        this.userId           = userId;
        this.roleId           = roleId;
        this.roleName         = roleName;
        this.fullName         = fullName;
        this.email            = email;
        this.passwordHash     = passwordHash;
        this.phone            = phone;
        this.profilePhoto     = profilePhoto;
        this.accountStatus    = accountStatus;
        this.isVerified       = isVerified;
        this.resetToken       = resetToken;
        this.resetTokenExpiry = resetTokenExpiry;
        this.lastLogin        = lastLogin;
        this.createdAt        = createdAt;
        this.updatedAt        = updatedAt;
    }

    // ---------------------------------------------------------------
    // Convenience Methods
    // ---------------------------------------------------------------

    /**
     * Returns the user's initials for avatar display.
     * Example: "Gbemiga Adeyemi" → "GA"
     */
    public String getInitials() {
        if (fullName == null || fullName.trim().isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    /**
     * Returns true if this user holds the ADMIN role.
     */
    public boolean isAdmin()    { return "ADMIN".equals(roleName); }

    /**
     * Returns true if this user holds the STUDENT role.
     */
    public boolean isStudent()  { return "STUDENT".equals(roleName); }

    /**
     * Returns true if this user holds the LANDLORD role.
     */
    public boolean isLandlord() { return "LANDLORD".equals(roleName); }

    /**
     * Returns true if this user holds the AGENT role.
     */
    public boolean isAgent()    { return "AGENT".equals(roleName); }

    /**
     * Returns true if this user holds the STAFF role.
     */
    public boolean isStaff()    { return "STAFF".equals(roleName); }

    /**
     * Returns true if the account is currently active.
     */
    public boolean isActive()   { return "ACTIVE".equals(accountStatus); }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getUserId()                         { return userId; }
    public void setUserId(int userId)              { this.userId = userId; }

    public int getRoleId()                         { return roleId; }
    public void setRoleId(int roleId)              { this.roleId = roleId; }

    public String getRoleName()                    { return roleName; }
    public void setRoleName(String roleName)       { this.roleName = roleName; }

    public String getFullName()                    { return fullName; }
    public void setFullName(String fullName)       { this.fullName = fullName; }

    public String getEmail()                       { return email; }
    public void setEmail(String email)             { this.email = email; }

    public String getPasswordHash()                { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone()                       { return phone; }
    public void setPhone(String phone)             { this.phone = phone; }

    public String getProfilePhoto()                { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getAccountStatus()               { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public boolean isVerified()                    { return isVerified; }
    public void setVerified(boolean isVerified)    { this.isVerified = isVerified; }

    public String getResetToken()                  { return resetToken; }
    public void setResetToken(String resetToken)   { this.resetToken = resetToken; }

    public Timestamp getResetTokenExpiry()                       { return resetTokenExpiry; }
    public void setResetTokenExpiry(Timestamp resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public Timestamp getLastLogin()                      { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin)        { this.lastLogin = lastLogin; }

    public Timestamp getCreatedAt()                      { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)        { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()                      { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt)        { this.updatedAt = updatedAt; }

    // ---------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "User{userId=" + userId +
               ", fullName='" + fullName + "'" +
               ", email='" + email + "'" +
               ", roleName='" + roleName + "'" +
               ", accountStatus='" + accountStatus + "'}";
    }
}