package com.realestate.dao;

import com.realestate.model.Inquiry;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * InquiryDAO - Data Access Object for the property_inquiries table.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class InquiryDAO {

    /**
     * Inserts a new inquiry. Also increments the property's inquiry_count.
     * Uses a transaction to keep both operations atomic.
     *
     * @param  inquiry  Populated Inquiry object
     * @return          Generated inquiry_id, or -1 on failure
     */
    public int createInquiry(Inquiry inquiry) {
        String insertSql = "INSERT INTO property_inquiries (property_id, sender_id, message) " +
                           "VALUES (?, ?, ?) RETURNING inquiry_id";
        String updateSql = "UPDATE properties SET inquiry_count = inquiry_count + 1 " +
                           "WHERE property_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin structural transaction

            int newId = -1;

            // Use try-with-resources for the statements within the running transaction
            try (PreparedStatement ps1 = conn.prepareStatement(insertSql);
                 PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                
                ps1.setInt(1, inquiry.getPropertyId());
                ps1.setInt(2, inquiry.getSenderId());
                ps1.setString(3, inquiry.getMessage());
                
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        newId = rs.getInt("inquiry_id");
                    }
                }

                ps2.setInt(1, inquiry.getPropertyId());
                ps2.executeUpdate();

                conn.commit(); // Commit atomic transaction blocks
                return newId;
            } catch (SQLException e) {
                if (conn != null) {
                    try { conn.rollback(); } catch (SQLException ignored) {}
                }
                throw e;
            } finally {
                if (conn != null) {
                    try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                }
            }
        } catch (SQLException e) {
            System.err.println("[InquiryDAO.createInquiry] Transaction Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
        return -1;
    }

    /**
     * Returns all inquiries received for properties owned by a given user.
     *
     * @param  ownerId  The property owner's user_id
     * @return          List of Inquiry objects with sender details
     */
    public List<Inquiry> findByOwner(int ownerId) {
        String sql = "SELECT i.*, " +
                     "       u.full_name AS sender_name, " +
                     "       u.email AS sender_email, " +
                     "       u.phone AS sender_phone, " +
                     "       p.title AS property_title " +
                     "FROM property_inquiries i " +
                     "JOIN users u ON i.sender_id = u.user_id " +
                     "JOIN properties p ON i.property_id = p.property_id " +
                     "WHERE p.owner_id = ? " +
                     "ORDER BY i.created_at DESC";

        return executeListQuery(sql, ownerId);
    }

    /**
     * Returns all inquiries sent by a specific user.
     *
     * @param  senderId  The inquirer's user_id
     * @return           List of Inquiry objects with property details
     */
    public List<Inquiry> findBySender(int senderId) {
        String sql = "SELECT i.*, " +
                     "       p.title AS property_title, " +
                     "       u.full_name AS owner_name, " +
                     "       '' AS sender_name, '' AS sender_email, '' AS sender_phone " +
                     "FROM property_inquiries i " +
                     "JOIN properties p ON i.property_id = p.property_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "WHERE i.sender_id = ? " +
                     "ORDER BY i.created_at DESC";

        return executeListQuery(sql, senderId);
    }

    /**
     * Records a reply from the property owner to an inquiry.
     *
     * @param  inquiryId    Target inquiry
     * @param  replyMessage The owner's reply text
     * @return              true if update succeeded
     */
    public boolean replyToInquiry(int inquiryId, String replyMessage) {
        String sql = "UPDATE property_inquiries SET " +
                     "reply_message = ?, inquiry_status = 'REPLIED', replied_at = NOW() " +
                     "WHERE inquiry_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, replyMessage);
            ps.setInt(2, inquiryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InquiryDAO.replyToInquiry] Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Marks an inquiry as READ.
     *
     * @param  inquiryId  Target inquiry
     * @return            true if update succeeded
     */
    public boolean markAsRead(int inquiryId) {
        String sql = "UPDATE property_inquiries SET inquiry_status = 'READ' " +
                     "WHERE inquiry_id = ? AND inquiry_status = 'UNREAD'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, inquiryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InquiryDAO.markAsRead] Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Counts unread inquiries for a property owner.
     *
     * @param  ownerId  The owner's user_id
     * @return          Count of unread inquiries
     */
    public int countUnreadByOwner(int ownerId) {
        String sql = "SELECT COUNT(*) FROM property_inquiries i " +
                     "JOIN properties p ON i.property_id = p.property_id " +
                     "WHERE p.owner_id = ? AND i.inquiry_status = 'UNREAD'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[InquiryDAO.countUnreadByOwner] Error: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------------------------------------------
    // Structural Data Helpers
    // ---------------------------------------------------------------

    private List<Inquiry> executeListQuery(String sql, int boundId) {
        List<Inquiry> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, boundId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[InquiryDAO.executeListQuery] Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Inquiry mapRow(ResultSet rs) throws SQLException {
        Inquiry i = new Inquiry();
        i.setInquiryId(rs.getInt("inquiry_id"));
        i.setPropertyId(rs.getInt("property_id"));
        i.setSenderId(rs.getInt("sender_id"));
        i.setMessage(rs.getString("message"));
        i.setInquiryStatus(rs.getString("inquiry_status"));
        i.setReplyMessage(rs.getString("reply_message"));
        i.setRepliedAt(rs.getTimestamp("replied_at"));
        i.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Contextual dynamic field mapping guards
        try { i.setSenderName(rs.getString("sender_name"));     } catch (SQLException ignored) {}
        try { i.setSenderEmail(rs.getString("sender_email"));   } catch (SQLException ignored) {}
        try { i.setSenderPhone(rs.getString("sender_phone"));   } catch (SQLException ignored) {}
        try { i.setPropertyTitle(rs.getString("property_title"));} catch (SQLException ignored) {}
        try { i.setOwnerName(rs.getString("owner_name"));       } catch (SQLException ignored) {}
        return i;
    }
}