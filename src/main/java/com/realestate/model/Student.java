package com.realestate.model;

import java.sql.Timestamp;

/**
 * Student - Maps to the students table.
 * Holds AOPE student-specific profile data.
 * Always associated with a User record via userId.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Student {

    private int       studentId;
    private int       userId;
    private String    matricNumber;
    private String    department;
    private String    level;       // ND1, ND2, HND1, HND2
    private String    faculty;
    private Timestamp createdAt;

    // Convenience field - populated by JOIN in DAO
    private User user;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Student() {}

    public Student(int userId, String matricNumber, String department,
                   String level, String faculty) {
        this.userId       = userId;
        this.matricNumber = matricNumber;
        this.department   = department;
        this.level        = level;
        this.faculty      = faculty;
    }

    public Student(int studentId, int userId, String matricNumber,
                   String department, String level, String faculty,
                   Timestamp createdAt) {
        this.studentId    = studentId;
        this.userId       = userId;
        this.matricNumber = matricNumber;
        this.department   = department;
        this.level        = level;
        this.faculty      = faculty;
        this.createdAt    = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getStudentId()                        { return studentId; }
    public void setStudentId(int studentId)          { this.studentId = studentId; }

    public int getUserId()                           { return userId; }
    public void setUserId(int userId)                { this.userId = userId; }

    public String getMatricNumber()                  { return matricNumber; }
    public void setMatricNumber(String matricNumber) { this.matricNumber = matricNumber; }

    public String getDepartment()                    { return department; }
    public void setDepartment(String department)     { this.department = department; }

    public String getLevel()                         { return level; }
    public void setLevel(String level)               { this.level = level; }

    public String getFaculty()                       { return faculty; }
    public void setFaculty(String faculty)           { this.faculty = faculty; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }

    public User getUser()                            { return user; }
    public void setUser(User user)                   { this.user = user; }

    // ---------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Student{studentId=" + studentId +
               ", userId=" + userId +
               ", matricNumber='" + matricNumber + "'" +
               ", department='" + department + "'" +
               ", level='" + level + "'}";
    }
}