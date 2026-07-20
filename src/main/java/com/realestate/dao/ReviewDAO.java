package com.realestate.dao;

import com.realestate.model.Review;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ReviewDAO - Data Access Object for property_reviews and property_ratings tables.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class ReviewDAO {

    /**
     * Inserts a new review (status = PENDING).
     *
     * @param  review  Populated Review object
     * @return         Generated review_id, or -1 on failure
     */
    public int createReview(Review review) {
        String sql = "INSERT INTO property_reviews " +
                     "(property_id, user_id, review_title, review_body) " +
                     "VALUES (?,?,?,?) RETURNING review_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, review.getPropertyId());
            ps.setInt(2, review.getUserId());
            ps.setString(3, review.getReviewTitle());
            ps.setString(4, review.getReviewBody());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("review_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.createReview] Production Error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Inserts or updates a property rating (upsert).
     * If the user has already rated this property, their rating is updated.
     *
     * @param  propertyId   Target property
     * @param  userId       Rater's user_id
     * @param  ratingValue  Star rating value (1–5)
     * @return              true if operation succeeded
     */
    public boolean saveRating(int propertyId, int userId, int ratingValue) {
        String sql = "INSERT INTO property_ratings (property_id, user_id, rating_value) " +
                     "VALUES (?,?,?) " +
                     "ON CONFLICT (property_id, user_id) " +
                     "DO UPDATE SET rating_value = EXCLUDED.rating_value, rated_at = NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, propertyId);
            ps.setInt(2, userId);
            ps.setInt(3, ratingValue);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.saveRating] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Returns all approved reviews for a property.
     *
     * @param  propertyId  Target property
     * @return             List of approved Review objects with reviewer name
     */
    public List<Review> findApprovedByProperty(int propertyId) {
        String sql = "SELECT r.*, u.full_name AS reviewer_name, u.profile_photo AS reviewer_photo, " +
                     "       COALESCE(pr.rating_value, 0) AS rating_value " +
                     "FROM property_reviews r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "LEFT JOIN property_ratings pr " +
                     "  ON pr.property_id = r.property_id AND pr.user_id = r.user_id " +
                     "WHERE r.property_id = ? AND r.review_status = 'APPROVED' " +
                     "ORDER BY r.created_at DESC";

        return executeListQuery(sql, new Object[]{propertyId});
    }

    /**
     * Returns all pending reviews (for admin moderation).
     *
     * @return  List of Review objects with PENDING status
     */
    public List<Review> findPending() {
        String sql = "SELECT r.*, u.full_name AS reviewer_name, " +
                     "       '' AS reviewer_photo, 0 AS rating_value " +
                     "FROM property_reviews r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "WHERE r.review_status = 'PENDING' " +
                     "ORDER BY r.created_at ASC";

        return executeListQuery(sql, new Object[]{});
    }

    /**
     * Approves or rejects a review (admin action).
     *
     * @param  reviewId  Target review
     * @param  status    APPROVED or REJECTED
     * @return           true if update succeeded
     */
    public boolean updateStatus(int reviewId, String status) {
        String sql = "UPDATE property_reviews SET review_status = ? WHERE review_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, reviewId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.updateStatus] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Returns the rating value the given user gave to the given property.
     *
     * @param  propertyId  Target property
     * @param  userId      Target user
     * @return             Rating value (1–5), or 0 if not yet rated
     */
    public int getUserRating(int propertyId, int userId) {
        String sql = "SELECT rating_value FROM property_ratings " +
                     "WHERE property_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, propertyId);
            ps.setInt(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating_value");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.getUserRating] Production Error: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private List<Review> executeListQuery(String sql, Object[] params) {
        List<Review> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[ReviewDAO.executeListQuery] Production Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getInt("review_id"));
        r.setPropertyId(rs.getInt("property_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setReviewTitle(rs.getString("review_title"));
        r.setReviewBody(rs.getString("review_body"));
        r.setReviewStatus(rs.getString("review_status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        
        try { r.setReviewerName(rs.getString("reviewer_name"));   } catch (SQLException ignored) {}
        try { r.setReviewerPhoto(rs.getString("reviewer_photo")); } catch (SQLException ignored) {}
        try { r.setRatingValue(rs.getInt("rating_value"));        } catch (SQLException ignored) {}
        
        return r;
    }
}