package com.realestate.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Appointment - Maps to the appointments table.
 * Represents a property viewing appointment request
 * made by a property seeker to a property owner.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Appointment {

    private int       appointmentId;
    private int       propertyId;
    private int       requesterId;
    private int       ownerId;
    private Date      preferredDate;
    private Time      preferredTime;
    private String    appointmentStatus;  // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String    notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Convenience fields
    private String    requesterName;
    private String    requesterPhone;
    private String    propertyTitle;
    private String    ownerName;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Appointment() {}

    public Appointment(int propertyId, int requesterId, int ownerId,
                       Date preferredDate, Time preferredTime, String notes) {
        this.propertyId    = propertyId;
        this.requesterId   = requesterId;
        this.ownerId       = ownerId;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.notes         = notes;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public boolean isPending()    { return "PENDING".equals(appointmentStatus); }
    public boolean isConfirmed()  { return "CONFIRMED".equals(appointmentStatus); }
    public boolean isCancelled()  { return "CANCELLED".equals(appointmentStatus); }
    public boolean isCompleted()  { return "COMPLETED".equals(appointmentStatus); }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getAppointmentId()                              { return appointmentId; }
    public void setAppointmentId(int appointmentId)            { this.appointmentId = appointmentId; }

    public int getPropertyId()                                 { return propertyId; }
    public void setPropertyId(int propertyId)                  { this.propertyId = propertyId; }

    public int getRequesterId()                                { return requesterId; }
    public void setRequesterId(int requesterId)                { this.requesterId = requesterId; }

    public int getOwnerId()                                    { return ownerId; }
    public void setOwnerId(int ownerId)                        { this.ownerId = ownerId; }

    public Date getPreferredDate()                             { return preferredDate; }
    public void setPreferredDate(Date preferredDate)           { this.preferredDate = preferredDate; }

    public Time getPreferredTime()                             { return preferredTime; }
    public void setPreferredTime(Time preferredTime)           { this.preferredTime = preferredTime; }

    public String getAppointmentStatus()                                   { return appointmentStatus; }
    public void setAppointmentStatus(String appointmentStatus)             { this.appointmentStatus = appointmentStatus; }

    public String getNotes()                                   { return notes; }
    public void setNotes(String notes)                         { this.notes = notes; }

    public Timestamp getCreatedAt()                            { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)              { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt()                            { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt)              { this.updatedAt = updatedAt; }

    public String getRequesterName()                           { return requesterName; }
    public void setRequesterName(String requesterName)         { this.requesterName = requesterName; }

    public String getRequesterPhone()                          { return requesterPhone; }
    public void setRequesterPhone(String requesterPhone)       { this.requesterPhone = requesterPhone; }

    public String getPropertyTitle()                           { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle)         { this.propertyTitle = propertyTitle; }

    public String getOwnerName()                               { return ownerName; }
    public void setOwnerName(String ownerName)                 { this.ownerName = ownerName; }

    @Override
    public String toString() {
        return "Appointment{appointmentId=" + appointmentId +
               ", propertyId=" + propertyId +
               ", preferredDate=" + preferredDate +
               ", status='" + appointmentStatus + "'}";
    }
}