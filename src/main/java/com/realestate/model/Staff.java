package com.realestate.model;

import java.sql.Timestamp;

/**
 * Staff - Maps to the staff table.
 * Holds AOPE staff-specific profile data.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Staff {

    private int       staffId;
    private int       userId;
    private String    staffNumber;
    private String    department;
    private String    designation;
    private Timestamp createdAt;

    // Convenience field - populated by JOIN in DAO
    private User user;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Staff() {}

    public Staff(int userId, String staffNumber,
                 String department, String designation) {
        this.userId      = userId;
        this.staffNumber = staffNumber;
        this.department  = department;
        this.designation = designation;
    }

    public Staff(int staffId, int userId, String staffNumber,
                 String department, String designation, Timestamp createdAt) {
        this.staffId     = staffId;
        this.userId      = userId;
        this.staffNumber = staffNumber;
        this.department  = department;
        this.designation = designation;
        this.createdAt   = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getStaffId()                          { return staffId; }
    public void setStaffId(int staffId)              { this.staffId = staffId; }

    public int getUserId()                           { return userId; }
    public void setUserId(int userId)                { this.userId = userId; }

    public String getStaffNumber()                   { return staffNumber; }
    public void setStaffNumber(String staffNumber)   { this.staffNumber = staffNumber; }

    public String getDepartment()                    { return department; }
    public void setDepartment(String department)     { this.department = department; }

    public String getDesignation()                   { return designation; }
    public void setDesignation(String designation)   { this.designation = designation; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }

    public User getUser()                            { return user; }
    public void setUser(User user)                   { this.user = user; }

    @Override
    public String toString() {
        return "Staff{staffId=" + staffId +
               ", staffNumber='" + staffNumber + "'" +
               ", designation='" + designation + "'}";
    }
}