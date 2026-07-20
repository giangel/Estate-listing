package com.realestate.servlet.agent;

import com.realestate.dao.InquiryDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.DBConnection;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * AgentDashboardServlet - Loads the agent dashboard.
 *
 * GET /agent/dashboard
 *   Loads:
 *     - Agent's verification status
 *     - All properties managed by this agent
 *     - Unread inquiries count
 *     - Notifications
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/agent/dashboard")
public class AgentDashboardServlet extends HttpServlet {

    private final PropertyDAO     propertyDAO     = new PropertyDAO();
    private final InquiryDAO      inquiryDAO      = new InquiryDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int agentUserId = SessionUtil.getLoggedInUserId(req);

        // ---------------------------------------------------------------
        // 1. Load agent record and verification status
        // ---------------------------------------------------------------
        String verificationStatus = "PENDING";
        String agencyName         = "";

        String sql = "SELECT verification_status, agency_name " +
                     "FROM agents WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, agentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    verificationStatus = rs.getString("verification_status");
                    agencyName         = rs.getString("agency_name");
                }
            }
        } catch (Exception e) {
            System.err.println("[AgentDashboardServlet] " + e.getMessage());
        }

        // ---------------------------------------------------------------
        // 2. Load agent's managed properties
        // ---------------------------------------------------------------
        List<Property> agentProperties = propertyDAO.findByOwner(agentUserId);

        // ---------------------------------------------------------------
        // 3. Performance totals
        // ---------------------------------------------------------------
        long totalListings  = agentProperties.size();
        long activeListings = agentProperties.stream()
            .filter(p -> "AVAILABLE".equals(p.getPropertyStatus()))
            .count();
        int totalInquiries  = agentProperties.stream()
            .mapToInt(Property::getInquiryCount).sum();
        int unreadInquiries = inquiryDAO.countUnreadByOwner(agentUserId);

        // ---------------------------------------------------------------
        // 4. Recent inquiries
        // ---------------------------------------------------------------
        req.setAttribute("recentInquiries",
            inquiryDAO.findByOwner(agentUserId)
                      .stream()
                      .limit(5)
                      .collect(java.util.stream.Collectors.toList()));

        // ---------------------------------------------------------------
        // 5. Notifications
        // ---------------------------------------------------------------
        req.setAttribute("notifications",
                          notificationDAO.findByUser(agentUserId));
        notificationDAO.markAllRead(agentUserId);

        // ---------------------------------------------------------------
        // 6. Pass to JSP
        // ---------------------------------------------------------------
        req.setAttribute("agentProperties",     agentProperties);
        req.setAttribute("verificationStatus",  verificationStatus);
        req.setAttribute("agencyName",          agencyName);
        req.setAttribute("totalListings",       totalListings);
        req.setAttribute("activeListings",      activeListings);
        req.setAttribute("totalInquiries",      totalInquiries);
        req.setAttribute("unreadInquiries",     unreadInquiries);
        req.setAttribute("pageTitle",           "Agent Dashboard");

        req.getRequestDispatcher("/agent/agent-dashboard.jsp")
           .forward(req, resp);
    }
}