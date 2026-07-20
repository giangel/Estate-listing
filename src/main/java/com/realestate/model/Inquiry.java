package com.realestate.model;

import java.sql.Timestamp;

/**
 * Inquiry - Maps to the property_inquiries table.
 * Represents a message sent by a property seeker to a property owner.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Inquiry {

    private int       inquiryId;
    private int       propertyId;
    private int       senderId;
    private String    message;
    private String    inquiryStatus;   // UNREAD, READ, REPLIED, CLOSED
    private String    replyMessage;
    private Timestamp repliedAt;
    private Timestamp createdAt;

    // Convenience fields joined from related tables
    private String    senderName;
    private String    senderEmail;
    private String    senderPhone;
    private String    propertyTitle;
    private String    ownerName;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Inquiry() {}

    public Inquiry(int propertyId, int senderId, String message) {
        this.propertyId = propertyId;
        this.senderId   = senderId;
        this.message    = message;
    }

    public Inquiry(int inquiryId, int propertyId, int senderId,
                   String message, String inquiryStatus,
                   String replyMessage, Timestamp repliedAt,
                   Timestamp createdAt) {
        this.inquiryId     = inquiryId;
        this.propertyId    = propertyId;
        this.senderId      = senderId;
        this.message       = message;
        this.inquiryStatus = inquiryStatus;
        this.replyMessage  = replyMessage;
        this.repliedAt     = repliedAt;
        this.createdAt     = createdAt;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public boolean isUnread()   { return "UNREAD".equals(inquiryStatus); }
    public boolean isReplied()  { return "REPLIED".equals(inquiryStatus); }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getInquiryId()                          { return inquiryId; }
    public void setInquiryId(int inquiryId)            { this.inquiryId = inquiryId; }

    public int getPropertyId()                         { return propertyId; }
    public void setPropertyId(int propertyId)          { this.propertyId = propertyId; }

    public int getSenderId()                           { return senderId; }
    public void setSenderId(int senderId)              { this.senderId = senderId; }

    public String getMessage()                         { return message; }
    public void setMessage(String message)             { this.message = message; }

    public String getInquiryStatus()                           { return inquiryStatus; }
    public void setInquiryStatus(String inquiryStatus)         { this.inquiryStatus = inquiryStatus; }

    public String getReplyMessage()                    { return replyMessage; }
    public void setReplyMessage(String replyMessage)   { this.replyMessage = replyMessage; }

    public Timestamp getRepliedAt()                    { return repliedAt; }
    public void setRepliedAt(Timestamp repliedAt)      { this.repliedAt = repliedAt; }

    public Timestamp getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)      { this.createdAt = createdAt; }

    public String getSenderName()                      { return senderName; }
    public void setSenderName(String senderName)       { this.senderName = senderName; }

    public String getSenderEmail()                     { return senderEmail; }
    public void setSenderEmail(String senderEmail)     { this.senderEmail = senderEmail; }

    public String getSenderPhone()                     { return senderPhone; }
    public void setSenderPhone(String senderPhone)     { this.senderPhone = senderPhone; }

    public String getPropertyTitle()                   { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

    public String getOwnerName()                       { return ownerName; }
    public void setOwnerName(String ownerName)         { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return "Inquiry{inquiryId=" + inquiryId +
               ", propertyId=" + propertyId +
               ", senderId=" + senderId +
               ", status='" + inquiryStatus + "'}";
    }
}