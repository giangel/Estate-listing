package com.realestate.dao;

import com.realestate.model.FraudReport;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FraudReportDAO - Data Access Object for the fraud_reports table.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class FraudReportDAO {

    /**
     * Inserts a new fraud report.
     *
     * @param  report  Populated FraudReport object
     * @return         true if insert succeeded
     */
    public boolean createReport(FraudReport report) {
        String sql = "INSERT INTO fraud_reports " +
                     "(property_id, reporter_id, report_reason, report_details) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, report.getPropertyId());
            ps.setInt(2, report.getReporterId());
            ps.setString(3, report.getReportReason());
            ps.setString(4, report.getReportDetails());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FraudReportDAO.createReport] Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Returns all fraud reports, optionally filtered by status.
     *
     * @param  statusFilter  Status string to filter by, or null for all
     * @return               List of FraudReport objects with related names
     */
    public List<FraudReport> findAll(String statusFilter) {
        StringBuilder sql = new StringBuilder(
            "SELECT fr.*, " +
            "       u.full_name AS reporter_name, " +
            "       p.title AS property_title " +
            "FROM fraud_reports fr " +
            "JOIN users u ON fr.reporter_id = u.user_id " +
            "JOIN properties p ON fr.property_id = p.property_id "
        );

        List<Object> params = new ArrayList<>();
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql.append("WHERE fr.report_status = ? ");
            params.add(statusFilter.trim());
        }
        sql.append("ORDER BY fr.created_at DESC");

        List<FraudReport> list = new ArrayList<>();

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
            System.err.println("[FraudReportDAO.findAll] Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Updates a fraud report's status and records the resolving admin.
     *
     * @param  reportId         Target report
     * @param  status           New status: INVESTIGATING, RESOLVED, DISMISSED
     * @param  adminId          Admin performing the action
     * @param  resolutionNotes  Admin's resolution notes
     * @return                  true if update succeeded
     */
    public boolean updateStatus(int reportId, String status, int adminId, String resolutionNotes) {
        String sql = "UPDATE fraud_reports SET " +
                     "report_status = ?, resolved_by = ?, " +
                     "resolution_notes = ?, resolved_at = CASE WHEN ? IN ('RESOLVED','DISMISSED') " +
                     "THEN NOW() ELSE NULL END " +
                     "WHERE report_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, adminId);
            ps.setString(3, resolutionNotes);
            ps.setString(4, status);
            ps.setInt(5, reportId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FraudReportDAO.updateStatus] Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Counts open fraud reports.
     * Used on the admin dashboard stat card.
     *
     * @return  Count of reports with status = OPEN
     */
    public int countOpen() {
        String sql = "SELECT COUNT(*) FROM fraud_reports WHERE report_status = 'OPEN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[FraudReportDAO.countOpen] Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns true if the given user has already reported the given property.
     * Prevents duplicate reports from the same user.
     */
    public boolean hasUserReported(int userId, int propertyId) {
        String sql = "SELECT 1 FROM fraud_reports " +
                     "WHERE reporter_id = ? AND property_id = ? AND report_status = 'OPEN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, propertyId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[FraudReportDAO.hasUserReported] Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Row Mapping Helper
    // ---------------------------------------------------------------

    private FraudReport mapRow(ResultSet rs) throws SQLException {
        FraudReport fr = new FraudReport();
        fr.setReportId(rs.getInt("report_id"));
        fr.setPropertyId(rs.getInt("property_id"));
        fr.setReporterId(rs.getInt("reporter_id"));
        fr.setReportReason(rs.getString("report_reason"));
        fr.setReportDetails(rs.getString("report_details"));
        fr.setReportStatus(rs.getString("report_status"));
        fr.setResolvedBy(rs.getInt("resolved_by"));
        fr.setResolutionNotes(rs.getString("resolution_notes"));
        fr.setCreatedAt(rs.getTimestamp("created_at"));
        fr.setResolvedAt(rs.getTimestamp("resolved_at"));
        
        try { fr.setReporterName(rs.getString("reporter_name"));   } catch (SQLException ignored) {}
        try { fr.setPropertyTitle(rs.getString("property_title")); } catch (SQLException ignored) {}
        return fr;
    }
}