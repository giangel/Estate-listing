package com.realestate.servlet.admin;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AgentVerificationServlet - Admin: verify or reject agent accounts.
 *
 * GET  /admin/verify-agent       → List all agents with their verification status
 * POST /admin/verify-agent       → Approve or reject an agent's verification
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/verify-agent")
public class AgentVerificationServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AuditLogDAO     auditLogDAO     = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String sql =
            "SELECT a.*, u.full_name, u.email, u.phone, u.created_at AS user_created " +
            "FROM agents a " +
            "JOIN users u ON a.user_id = u.user_id " +
            "ORDER BY CASE a.verification_status " +
            "WHEN 'PENDING' THEN 1 WHEN 'VERIFIED' THEN 2 ELSE 3 END, " +
            "a.created_at ASC";

        List<Map<String, Object>> agents = new ArrayList<>();
        
        try (Connection c = com.realestate.util.DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("agentId",            rs.getInt("agent_id"));
                row.put("userId",             rs.getInt("user_id"));
                row.put("fullName",           rs.getString("full_name"));
                row.put("email",              rs.getString("email"));
                row.put("phone",              rs.getString("phone"));
                row.put("agencyName",         rs.getString("agency_name"));
                row.put("licenseNumber",      rs.getString("license_number"));
                row.put("verificationStatus", rs.getString("verification_status"));
                row.put("verifiedAt",         rs.getTimestamp("verified_at"));
                agents.add(row);
            }
        } catch (SQLException e) {
            System.err.println("[AgentVerificationServlet.doGet] Database execution error: " + e.getMessage());
        }

        req.setAttribute("agents",    agents);
        req.setAttribute("pageTitle", "Agent Verification");
        req.getRequestDispatcher("/admin/verify-agents.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // Strict production CSRF protection check
        if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }

        req.setCharacterEncoding("UTF-8");
        int adminId   = SessionUtil.getLoggedInUserId(req);
        int agentId   = parseIntSafe(req.getParameter("agentId"), 0);
        String action = ValidationUtil.safeTrim(req.getParameter("action"));

        if (agentId <= 0 || action.isEmpty()) {
            SessionUtil.setErrorMessage(req, "Invalid processing request parameters.");
            resp.sendRedirect(req.getContextPath() + "/admin/verify-agent");
            return;
        }

        String newStatus   = "verify".equalsIgnoreCase(action) ? "VERIFIED" : "REJECTED";
        String auditAction = "verify".equalsIgnoreCase(action) ? "VERIFY_AGENT" : "REJECT_AGENT";

        // Query segments decoupled into two distinct executions to avoid driver compatibility crashes
        String updateSql = "UPDATE agents SET verification_status = ?, verified_by = ?, verified_at = NOW() WHERE agent_id = ?";
        String selectSql = "SELECT user_id FROM agents WHERE agent_id = ?";

        int agentUserId = -1;
        
        try (Connection c = com.realestate.util.DBConnection.getConnection()) {
            // Disable AutoCommit to run as an isolated transaction boundary
            c.setAutoCommit(false);
            
            try (PreparedStatement updatePs = c.prepareStatement(updateSql);
                 PreparedStatement selectPs = c.prepareStatement(selectSql)) {
                
                // 1. Run Status Update Map
                updatePs.setString(1, newStatus);
                updatePs.setInt(2,    adminId);
                updatePs.setInt(3,    agentId);
                int rowsUpdated = updatePs.executeUpdate();
                
                if (rowsUpdated > 0) {
                    // 2. Fetch Owner Identity Matrix
                    selectPs.setInt(1, agentId);
                    try (ResultSet rs = selectPs.executeQuery()) {
                        if (rs.next()) {
                            agentUserId = rs.getInt("user_id");
                        }
                    }
                }
                c.commit(); // Execution succeeded
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("[AgentVerificationServlet.doPost] Critical execution rollback triggered: " + e.getMessage());
        }

        if (agentUserId > 0) {
            // Build Context Messages
            String title = "VERIFIED".equals(newStatus) ? "Agent Account Verified" : "Agent Verification Rejected";
            String message = "VERIFIED".equals(newStatus)
                ? "Congratulations! Your agent account has been verified. You can now manage properties on behalf of landlords."
                : "Your agent verification request was not approved. Please contact the administrator for more information.";

            com.realestate.model.Notification notif = new com.realestate.model.Notification(
                agentUserId, title, message, "SYSTEM", agentId
            );
            notificationDAO.createNotification(notif);

            auditLogDAO.log(adminId, auditAction, "AGENT", agentId,
                            "Agent #" + agentId + " → " + newStatus,
                            req.getRemoteAddr());

            SessionUtil.setSuccessMessage(req, "Agent verification status successfully updated to: " + newStatus);
        } else {
            SessionUtil.setErrorMessage(req, "The requested verification update operation failed.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/verify-agent");
    }

    private int parseIntSafe(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try { 
            return Integer.parseInt(val.trim()); 
        } catch (NumberFormatException e) { 
            return def; 
        }
    }
}