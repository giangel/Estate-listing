package com.realestate.dao;

import com.realestate.model.RoommateProfile;
import com.realestate.model.RoommateRequest;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RoommateDAO - Data Access Object for roommate_profiles and roommate_requests.
 *
 * @author  AOPE CS Department
 * @version 1.2
 */
public class RoommateDAO {

    // ---------------------------------------------------------------
    // PROFILE CRUD
    // ---------------------------------------------------------------

    /**
     * Creates or updates a roommate profile (upsert by user_id).
     *
     * @param  profile  Populated RoommateProfile object
     * @return          true if operation succeeded
     */
    public boolean saveProfile(RoommateProfile profile) {
        String sql = "INSERT INTO roommate_profiles " +
                     "(user_id, gender_preference, budget_min, budget_max, " +
                     " preferred_area, department, level, description, is_active) " +
                     "VALUES (?,?,?,?,?,?,?,?,TRUE) " +
                     "ON CONFLICT (user_id) DO UPDATE SET " +
                     "gender_preference = EXCLUDED.gender_preference, " +
                     "budget_min = EXCLUDED.budget_min, " +
                     "budget_max = EXCLUDED.budget_max, " +
                     "preferred_area = EXCLUDED.preferred_area, " +
                     "department = EXCLUDED.department, " +
                     "level = EXCLUDED.level, " +
                     "description = EXCLUDED.description, " +
                     "updated_at = NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getGenderPreference());

            if (profile.getBudgetMin() != null) {
                ps.setBigDecimal(3, profile.getBudgetMin());
            } else {
                ps.setNull(3, Types.NUMERIC);
            }

            if (profile.getBudgetMax() != null) {
                ps.setBigDecimal(4, profile.getBudgetMax());
            } else {
                ps.setNull(4, Types.NUMERIC);
            }

            ps.setString(5, profile.getPreferredArea());
            ps.setString(6, profile.getDepartment());
            ps.setString(7, profile.getLevel());
            ps.setString(8, profile.getDescription());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoommateDAO.saveProfile] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves the roommate profile for a given user.
     *
     * @param  userId  Target user
     * @return         RoommateProfile object, or null if not found
     */
    public RoommateProfile findProfileByUser(int userId) {
        String sql = "SELECT rp.*, u.full_name AS user_name, u.profile_photo " +
                     "FROM roommate_profiles rp " +
                     "JOIN users u ON rp.user_id = u.user_id " +
                     "WHERE rp.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProfileRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[RoommateDAO.findProfileByUser] Production Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns roommate profile matches for a given user.
     * Matches based on compatible gender preference, overlapping budget,
     * and same level. Excludes the requesting user and users they've
     * already sent requests to.
     *
     * @param  profile  The requesting user's profile
     * @return          List of compatible RoommateProfile objects with match scores
     */
    public List<RoommateProfile> findMatches(RoommateProfile profile) {
        String sql = "SELECT rp.*, u.full_name AS user_name, u.profile_photo " +
                     "FROM roommate_profiles rp " +
                     "JOIN users u ON rp.user_id = u.user_id " +
                     "WHERE rp.user_id != ? " +
                     "AND rp.is_active = TRUE " +
                     "AND rp.user_id NOT IN (" +
                     "    SELECT receiver_id FROM roommate_requests WHERE sender_id = ?" +
                     ") " +
                     "AND (rp.gender_preference = 'ANY' OR ? = 'ANY' OR rp.gender_preference = ?) " +
                     "ORDER BY rp.updated_at DESC " +
                     "LIMIT 20";

        List<RoommateProfile> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profile.getUserId());
            ps.setInt(2, profile.getUserId());
            ps.setString(3, profile.getGenderPreference());
            ps.setString(4, profile.getGenderPreference());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoommateProfile match = mapProfileRow(rs);
                    match.setMatchScore(calculateScore(profile, match));
                    list.add(match);
                }
            }

            // Sort by match score descending
            list.sort((a, b) -> b.getMatchScore() - a.getMatchScore());

            return list;
        } catch (SQLException e) {
            System.err.println("[RoommateDAO.findMatches] Production Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Calculates a compatibility score between two roommate profiles.
     * Maximum score = 100.
     */
    private int calculateScore(RoommateProfile requester, RoommateProfile candidate) {
        int score = 0;

        // Gender preference compatibility (+25)
        if ("ANY".equals(requester.getGenderPreference()) ||
            "ANY".equals(candidate.getGenderPreference()) ||
            requester.getGenderPreference().equals(candidate.getGenderPreference())) {
            score += 25;
        }

        // Budget overlap (+25)
        if (requester.getBudgetMin() != null && requester.getBudgetMax() != null &&
            candidate.getBudgetMin() != null && candidate.getBudgetMax() != null) {
            boolean overlap = requester.getBudgetMin().compareTo(candidate.getBudgetMax()) <= 0 &&
                              candidate.getBudgetMin().compareTo(requester.getBudgetMax()) <= 0;
            if (overlap) score += 25;
        } else {
            score += 15; // Partial credit if budget not fully specified
        }

        // Same level (+25)
        if (requester.getLevel() != null && requester.getLevel().equals(candidate.getLevel())) {
            score += 25;
        }

        // Same department (+25)
        if (requester.getDepartment() != null && requester.getDepartment().equalsIgnoreCase(candidate.getDepartment())) {
            score += 25;
        }

        return Math.min(score, 100);
    }

    // ---------------------------------------------------------------
    // REQUEST CRUD
    // ---------------------------------------------------------------

    /**
     * Sends a roommate connection request.
     *
     * @param  request  Populated RoommateRequest object
     * @return          true if insert succeeded
     */
    public boolean sendRequest(RoommateRequest request) {
        String sql = "INSERT INTO roommate_requests (sender_id, receiver_id, message) " +
                     "VALUES (?,?,?) " +
                     "ON CONFLICT (sender_id, receiver_id) DO NOTHING";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, request.getSenderId());
            ps.setInt(2, request.getReceiverId());
            ps.setString(3, request.getMessage());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoommateDAO.sendRequest] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a specific roommate connection request by its unique tracking identity.
     * Used for validating context security parameters on responses.
     *
     * @param requestId Target tracking identity
     * @return Populated RoommateRequest object, or null if missing from persistence layers
     */
    public RoommateRequest findRequestById(int requestId) {
        String sql = "SELECT * FROM roommate_requests WHERE request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RoommateRequest rr = new RoommateRequest();
                    rr.setRequestId(rs.getInt("request_id"));
                    rr.setSenderId(rs.getInt("sender_id"));
                    rr.setReceiverId(rs.getInt("receiver_id"));
                    rr.setMessage(rs.getString("message"));
                    rr.setRequestStatus(rs.getString("request_status"));
                    rr.setCreatedAt(rs.getTimestamp("created_at"));
                    rr.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return rr;
                }
            }
        } catch (SQLException e) {
            System.err.println("[RoommateDAO.findRequestById] Production Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all incoming roommate requests for a user.
     *
     * @param  receiverId  Target user
     * @return             List of RoommateRequest objects with sender details
     */
    public List<RoommateRequest> findIncomingRequests(int receiverId) {
        String sql = "SELECT rr.*, " +
                     "       u.full_name AS sender_name, " +
                     "       s.department AS sender_department, " +
                     "       s.level AS sender_level " +
                     "FROM roommate_requests rr " +
                     "JOIN users u ON rr.sender_id = u.user_id " +
                     "LEFT JOIN students s ON u.user_id = s.user_id " +
                     "WHERE rr.receiver_id = ? AND rr.request_status = 'PENDING' " +
                     "ORDER BY rr.created_at DESC";

        return executeRequestListQuery(sql, new Object[]{receiverId});
    }

    /**
     * Updates the status of a roommate request.
     *
     * @param  requestId  Target request
     * @param  status      ACCEPTED or DECLINED
     * @return            true if update succeeded
     */
    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE roommate_requests SET request_status = ?, updated_at = NOW() " +
                     "WHERE request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, requestId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoommateDAO.updateRequestStatus] Production Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private List<RoommateRequest> executeRequestListQuery(String sql, Object[] params) {
        List<RoommateRequest> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoommateRequest rr = new RoommateRequest();
                    rr.setRequestId(rs.getInt("request_id"));
                    rr.setSenderId(rs.getInt("sender_id"));
                    rr.setReceiverId(rs.getInt("receiver_id"));
                    rr.setMessage(rs.getString("message"));
                    rr.setRequestStatus(rs.getString("request_status"));
                    rr.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    try { rr.setSenderName(rs.getString("sender_name"));             } catch (SQLException ignored) {}
                    try { rr.setSenderDepartment(rs.getString("sender_department")); } catch (SQLException ignored) {}
                    try { rr.setSenderLevel(rs.getString("sender_level"));           } catch (SQLException ignored) {}
                    
                    list.add(rr);
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[RoommateDAO.executeRequestListQuery] Production Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private RoommateProfile mapProfileRow(ResultSet rs) throws SQLException {
        RoommateProfile rp = new RoommateProfile();
        rp.setRoommateProfileId(rs.getInt("roommate_profile_id"));
        rp.setUserId(rs.getInt("user_id"));
        rp.setGenderPreference(rs.getString("gender_preference"));
        rp.setBudgetMin(rs.getBigDecimal("budget_min"));
        rp.setBudgetMax(rs.getBigDecimal("budget_max"));
        rp.setPreferredArea(rs.getString("preferred_area"));
        rp.setDepartment(rs.getString("department"));
        rp.setLevel(rs.getString("level"));
        rp.setDescription(rs.getString("description"));
        rp.setActive(rs.getBoolean("is_active"));
        rp.setCreatedAt(rs.getTimestamp("created_at"));
        rp.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        try { rp.setUserName(rs.getString("user_name"));       } catch (SQLException ignored) {}
        try { rp.setProfilePhoto(rs.getString("profile_photo")); } catch (SQLException ignored) {}
        
        return rp;
    }
}