package com.realestate.model;

import java.sql.Timestamp;

/**
 * Role - Maps to the roles table.
 * Represents a system role assigned to each user.
 * Possible values: ADMIN, STUDENT, STAFF, LANDLORD, AGENT
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class Role {

    private int       roleId;
    private String    roleName;
    private String    roleDescription;
    private Timestamp createdAt;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Role() {}

    public Role(int roleId, String roleName, String roleDescription, Timestamp createdAt) {
        this.roleId          = roleId;
        this.roleName        = roleName;
        this.roleDescription = roleDescription;
        this.createdAt       = createdAt;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public int getRoleId()                    { return roleId; }
    public void setRoleId(int roleId)         { this.roleId = roleId; }

    public String getRoleName()               { return roleName; }
    public void setRoleName(String roleName)  { this.roleName = roleName; }

    public String getRoleDescription()                        { return roleDescription; }
    public void setRoleDescription(String roleDescription)   { this.roleDescription = roleDescription; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }

    // ---------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Role{roleId=" + roleId + ", roleName='" + roleName + "'}";
    }
}