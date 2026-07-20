package com.realestate.dao;

import com.realestate.model.AuditLog;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AuditLogDAO - Data Access Object for the audit_logs table.
 *
 * Provides write-only insert (logs are never edited or deleted)
 * and admin read access for the audit log viewer.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class AuditLogDAO {

    /**
     * Inserts a new audit log entry.
     * This method never throws - audit failures must not interrupt business logic.
     *
     * @param  log  Populated AuditLog object
     */
    public void log(AuditLog log) {
        String sql = "INSERT INTO audit_logs " +
                     "(user_id, action, entity_type, entity_id, description, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (log.getUserId() > 0) {
                ps.setInt(1, log.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            ps.setString(2, log.getAction());
            ps.setString(3, log.getEntityType());

            if (log.getEntityId() > 0) {
                ps.setInt(4, log.getEntityId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, log.getDescription());
            ps.setString(6, log.getIpAddress());
            ps.executeUpdate();

        } catch (SQLException e) {
            // Log to console - do not rethrow; audit failures are non-fatal
            System.err.println("[AuditLogDAO.log] Warning: " + e.getMessage());
        }
    }

    /**
     * Convenience method to log an action without creating an AuditLog object.
     *
     * @param  userId      User performing the action (0 for anonymous)
     * @param  action      Action name constant (e.g. "LOGIN")
     * @param  entityType  Entity type (e.g. "PROPERTY") or null
     * @param  entityId    Entity primary key or 0 if not applicable
     * @param  description Human-readable description
     * @param  ipAddress   Client IP address
     */
    public void log(int userId, String action, String entityType,
                    int entityId, String description, String ipAddress) {
        AuditLog entry = new AuditLog(userId, action, entityType,
                                       entityId, description, ipAddress);
        log(entry);
    }

    /**
     * Returns the most recent audit log entries for admin viewing.
     *
     * @param  limit   Maximum number of entries to return
     * @param  offset  Offset for pagination
     * @return         List of AuditLog objects with user names
     */
    public List<AuditLog> findRecent(int limit, int offset) {
        String sql = "SELECT al.*, u.full_name AS user_name " +
                     "FROM audit_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "ORDER BY al.created_at DESC " +
                     "LIMIT ? OFFSET ?";

        List<AuditLog> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[AuditLogDAO.findRecent] Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns audit logs filtered by action type and/or user ID.
     *
     * @param  actionFilter  Action name to filter by, or null for all
     * @param  userIdFilter  User ID to filter by, or 0 for all
     * @param  limit         Maximum results
     * @return               List of matching AuditLog objects
     */
    public List<AuditLog> findFiltered(String actionFilter, int userIdFilter, int limit) {
        StringBuilder sql = new StringBuilder(
            "SELECT al.*, u.full_name AS user_name " +
            "FROM audit_logs al " +
            "LEFT JOIN users u ON al.user_id = u.user_id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (actionFilter != null && !actionFilter.trim().isEmpty()) {
            sql.append("AND al.action = ? ");
            params.add(actionFilter.trim());
        }

        if (userIdFilter > 0) {
            sql.append("AND al.user_id = ? ");
            params.add(userIdFilter);
        }

        sql.append("ORDER BY al.created_at DESC LIMIT ?");
        params.add(limit);

        List<AuditLog> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[AuditLogDAO.findFiltered] Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns the total count of audit log entries.
     * Used for pagination in the admin log viewer.
     *
     * @return  Total log entry count
     */
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM audit_logs";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[AuditLogDAO.countTotal] Error: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------------------------------------------
    // Row Mapping Helper
    // ---------------------------------------------------------------

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("log_id"));
        log.setUserId(rs.getInt("user_id"));
        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entity_type"));
        log.setEntityId(rs.getInt("entity_id"));
        log.setDescription(rs.getString("description"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        
        try { 
            log.setUserName(rs.getString("user_name")); 
        } catch (SQLException ignored) {}
        
        return log;
    }
}