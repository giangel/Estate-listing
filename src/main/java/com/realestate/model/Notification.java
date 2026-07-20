package com.realestate.model;

import java.sql.Timestamp;

/**
 * Notification - Maps to the notifications table.
 * Represents an in-app notification delivered to a user.
 * Types: INQUIRY, APPROVAL, APPOINTMENT, FRAUD, SYSTEM, ROOMMATE
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Notification {

    private int       notificationId;
    private int       userId;
    private String    title;
    private String    message;
    private String    notificationType;
    private int       referenceId;
    private boolean   isRead;
    private Timestamp createdAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Notification() {}

    public Notification(int userId, String title, String message,
                        String notificationType, int referenceId) {
        this.userId           = userId;
        this.title            = title;
        this.message          = message;
        this.notificationType = notificationType;
        this.referenceId      = referenceId;
    }

    public Notification(int notificationId, int userId, String title,
                        String message, String notificationType,
                        int referenceId, boolean isRead, Timestamp createdAt) {
        this.notificationId   = notificationId;
        this.userId           = userId;
        this.title            = title;
        this.message          = message;
        this.notificationType = notificationType;
        this.referenceId      = referenceId;
        this.isRead           = isRead;
        this.createdAt        = createdAt;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public String getTypeIcon() {
        if (notificationType == null) return "bi-bell";
        switch (notificationType) {
            case "INQUIRY":     return "bi-envelope";
            case "APPROVAL":    return "bi-check-circle";
            case "APPOINTMENT": return "bi-calendar-check";
            case "FRAUD":       return "bi-shield-exclamation";
            case "ROOMMATE":    return "bi-people";
            default:            return "bi-info-circle";
        }
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getNotificationId()                             { return notificationId; }
    public void setNotificationId(int notificationId)          { this.notificationId = notificationId; }

    public int getUserId()                                     { return userId; }
    public void setUserId(int userId)                          { this.userId = userId; }

    public String getTitle()                                   { return title; }
    public void setTitle(String title)                         { this.title = title; }

    public String getMessage()                                 { return message; }
    public void setMessage(String message)                     { this.message = message; }

    public String getNotificationType()                                    { return notificationType; }
    public void setNotificationType(String notificationType)               { this.notificationType = notificationType; }

    public int getReferenceId()                                { return referenceId; }
    public void setReferenceId(int referenceId)                { this.referenceId = referenceId; }

    public boolean isRead()                                    { return isRead; }
    public void setRead(boolean isRead)                        { this.isRead = isRead; }

    public Timestamp getCreatedAt()                            { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)              { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Notification{notificationId=" + notificationId +
               ", userId=" + userId +
               ", title='" + title + "'" +
               ", isRead=" + isRead + "}";
    }
}