package com.realestate.model;

import java.sql.Timestamp;

/**
 * FraudReport - Maps to the fraud_reports table.
 * Represents a fraud complaint filed by a user against a property listing.
 * Statuses: OPEN → INVESTIGATING → RESOLVED or DISMISSED
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class FraudReport {

    private int       reportId;
    private int       propertyId;
    private int       reporterId;
    private String    reportReason;   // DUPLICATE, FAKE, OVERPRICED, OCCUPIED, OTHER
    private String    reportDetails;
    private String    reportStatus;   // OPEN, INVESTIGATING, RESOLVED, DISMISSED
    private int       resolvedBy;
    private String    resolutionNotes;
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    // Convenience fields
    private String    reporterName;
    private String    propertyTitle;
    private String    resolvedByName;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public FraudReport() {}

    public FraudReport(int propertyId, int reporterId,
                       String reportReason, String reportDetails) {
        this.propertyId    = propertyId;
        this.reporterId    = reporterId;
        this.reportReason  = reportReason;
        this.reportDetails = reportDetails;
    }

    // ---------------------------------------------------------------
    // Convenience
    // ---------------------------------------------------------------

    public boolean isOpen()          { return "OPEN".equals(reportStatus); }
    public boolean isInvestigating() { return "INVESTIGATING".equals(reportStatus); }
    public boolean isResolved()      { return "RESOLVED".equals(reportStatus); }
    public boolean isDismissed()     { return "DISMISSED".equals(reportStatus); }

    public String getStatusBadgeClass() {
        if (reportStatus == null) return "re-badge-pending";
        switch (reportStatus) {
            case "OPEN":          return "re-badge-occupied";
            case "INVESTIGATING": return "re-badge-pending";
            case "RESOLVED":      return "re-badge-available";
            case "DISMISSED":     return "re-badge-reserved";
            default:              return "re-badge-pending";
        }
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getReportId()                             { return reportId; }
    public void setReportId(int reportId)                { this.reportId = reportId; }

    public int getPropertyId()                           { return propertyId; }
    public void setPropertyId(int propertyId)            { this.propertyId = propertyId; }

    public int getReporterId()                           { return reporterId; }
    public void setReporterId(int reporterId)            { this.reporterId = reporterId; }

    public String getReportReason()                      { return reportReason; }
    public void setReportReason(String reportReason)     { this.reportReason = reportReason; }

    public String getReportDetails()                     { return reportDetails; }
    public void setReportDetails(String reportDetails)   { this.reportDetails = reportDetails; }

    public String getReportStatus()                      { return reportStatus; }
    public void setReportStatus(String reportStatus)     { this.reportStatus = reportStatus; }

    public int getResolvedBy()                           { return resolvedBy; }
    public void setResolvedBy(int resolvedBy)            { this.resolvedBy = resolvedBy; }

    public String getResolutionNotes()                           { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes)       { this.resolutionNotes = resolutionNotes; }

    public Timestamp getCreatedAt()                      { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)        { this.createdAt = createdAt; }

    public Timestamp getResolvedAt()                     { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt)      { this.resolvedAt = resolvedAt; }

    public String getReporterName()                      { return reporterName; }
    public void setReporterName(String reporterName)     { this.reporterName = reporterName; }

    public String getPropertyTitle()                     { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle)   { this.propertyTitle = propertyTitle; }

    public String getResolvedByName()                    { return resolvedByName; }
    public void setResolvedByName(String resolvedByName) { this.resolvedByName = resolvedByName; }

    @Override
    public String toString() {
        return "FraudReport{reportId=" + reportId +
               ", propertyId=" + propertyId +
               ", reportReason='" + reportReason + "'" +
               ", status='" + reportStatus + "'}";
    }
}