package com.realestate.model;

import java.sql.Timestamp;

/**
 * RoommateRequest - Maps to the roommate_requests table.
 * Represents a connection request sent from one student to another
 * after being matched by the roommate engine.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class RoommateRequest {

    private int       requestId;
    private int       senderId;
    private int       receiverId;
    private String    message;
    private String    requestStatus;  // PENDING, ACCEPTED, DECLINED
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Convenience fields
    private String    senderName;
    private String    senderDepartment;
    private String    senderLevel;
    private String    receiverName;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public RoommateRequest() {}

    public RoommateRequest(int senderId, int receiverId, String message) {
        this.senderId   = senderId;
        this.receiverId = receiverId;
        this.message    = message;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public boolean isPending()  { return "PENDING".equals(requestStatus); }
    public boolean isAccepted() { return "ACCEPTED".equals(requestStatus); }
    public boolean isDeclined() { return "DECLINED".equals(requestStatus); }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getRequestId()                            { return requestId; }
    public void setRequestId(int requestId)              { this.requestId = requestId; }

    public int getSenderId()                             { return senderId; }
    public void setSenderId(int senderId)                { this.senderId = senderId; }

    public int getReceiverId()                           { return receiverId; }
    public void setReceiverId(int receiverId)            { this.receiverId = receiverId; }

    public String getMessage()                           { return message; }
    public void setMessage(String message)               { this.message = message; }

    public String getRequestStatus()                     { return requestStatus; }
    public void setRequestStatus(String requestStatus)   { this.requestStatus = requestStatus; }

    public Timestamp getCreatedAt()                      { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)        { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()                      { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt)        { this.updatedAt = updatedAt; }

    public String getSenderName()                        { return senderName; }
    public void setSenderName(String senderName)         { this.senderName = senderName; }

    public String getSenderDepartment()                          { return senderDepartment; }
    public void setSenderDepartment(String senderDepartment)     { this.senderDepartment = senderDepartment; }

    public String getSenderLevel()                       { return senderLevel; }
    public void setSenderLevel(String senderLevel)       { this.senderLevel = senderLevel; }

    public String getReceiverName()                      { return receiverName; }
    public void setReceiverName(String receiverName)     { this.receiverName = receiverName; }

    @Override
    public String toString() {
        return "RoommateRequest{requestId=" + requestId +
               ", senderId=" + senderId +
               ", receiverId=" + receiverId +
               ", status='" + requestStatus + "'}";
    }
}