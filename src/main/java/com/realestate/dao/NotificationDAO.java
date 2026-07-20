package com.realestate.dao;

import com.realestate.model.Notification;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NotificationDAO - Data Access Object for the notifications table.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class NotificationDAO {

    /**
     * Inserts a new notification for a user.
     *
     * @param  n  Populated Notification object
     * @return    true if insert succeeded
     */
    public boolean createNotification(Notification n) {
        String sql = "INSERT INTO notifications " +
                     "(user_id, title, message, notification_type, reference_id) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getMessage());
            ps.setString(4, n.getNotificationType());

            if (n.getReferenceId() > 0) {
                ps.setInt(5, n.getReferenceId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.createNotification] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Returns all notifications for a user (newest first, limit 20).
     *
     * @param  userId  Target user
     * @return         List of Notification objects
     */
    public List<Notification> findByUser(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? " +
                     "ORDER BY created_at DESC LIMIT 20";

        List<Notification> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.findByUser] Production Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns the count of unread notifications for a user.
     * Used for the navbar notification badge.
     *
     * @param  userId  Target user
     * @return         Unread notification count
     */
    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.countUnread] Production Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Marks all of a user's notifications as read.
     *
     * @param  userId  Target user
     * @return         true if update succeeded
     */
    public boolean markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE " +
                     "WHERE user_id = ? AND is_read = FALSE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.markAllRead] Production Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Row Mapping Helper
    // ---------------------------------------------------------------

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getInt("notification_id"));
        n.setUserId(rs.getInt("user_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setNotificationType(rs.getString("notification_type"));
        n.setReferenceId(rs.getInt("reference_id"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }
}