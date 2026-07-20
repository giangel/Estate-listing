package com.realestate.model;

import java.sql.Timestamp;

/**
 * AuditLog - Maps to the audit_logs table.
 * Records every significant system event for administrative
 * review, security monitoring, and compliance purposes.
 *
 * Common action values:
 *   LOGIN, LOGOUT, REGISTER
 *   CREATE_PROPERTY, UPDATE_PROPERTY, DELETE_PROPERTY
 *   APPROVE_PROPERTY, REJECT_PROPERTY
 *   VERIFY_AGENT, REJECT_AGENT
 *   SUBMIT_INQUIRY, SUBMIT_REVIEW
 *   REPORT_FRAUD, RESOLVE_FRAUD
 *   SUSPEND_USER, ACTIVATE_USER
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class AuditLog {

    private int       logId;
    private int       userId;
    private String    action;
    private String    entityType;
    private int       entityId;
    private String    description;
    private String    ipAddress;
    private Timestamp createdAt;

    // Convenience field
    private String    userName;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public AuditLog() {}

    public AuditLog(int userId, String action, String entityType,
                    int entityId, String description, String ipAddress) {
        this.userId      = userId;
        this.action      = action;
        this.entityType  = entityType;
        this.entityId    = entityId;
        this.description = description;
        this.ipAddress   = ipAddress;
    }

    public AuditLog(int logId, int userId, String action, String entityType,
                    int entityId, String description,
                    String ipAddress, Timestamp createdAt) {
        this.logId       = logId;
        this.userId      = userId;
        this.action      = action;
        this.entityType  = entityType;
        this.entityId    = entityId;
        this.description = description;
        this.ipAddress   = ipAddress;
        this.createdAt   = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getLogId()                            { return logId; }
    public void setLogId(int logId)                  { this.logId = logId; }

    public int getUserId()                           { return userId; }
    public void setUserId(int userId)                { this.userId = userId; }

    public String getAction()                        { return action; }
    public void setAction(String action)             { this.action = action; }

    public String getEntityType()                    { return entityType; }
    public void setEntityType(String entityType)     { this.entityType = entityType; }

    public int getEntityId()                         { return entityId; }
    public void setEntityId(int entityId)            { this.entityId = entityId; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public String getIpAddress()                     { return ipAddress; }
    public void setIpAddress(String ipAddress)       { this.ipAddress = ipAddress; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }

    public String getUserName()                      { return userName; }
    public void setUserName(String userName)         { this.userName = userName; }

    @Override
    public String toString() {
        return "AuditLog{logId=" + logId +
               ", userId=" + userId +
               ", action='" + action + "'" +
               ", entityType='" + entityType + "'" +
               ", entityId=" + entityId + "}";
    }
}