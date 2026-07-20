package com.realestate.servlet.admin;

import com.realestate.dao.*;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * AdminDashboardServlet - Loads the admin dashboard with platform statistics.
 *
 * GET  /admin/dashboard              → Loads full dashboard page
 * GET  /admin/dashboard?action=notif_count → Returns JSON notification count
 * (shared with all authenticated users
 * via navbar polling)
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UserDAO         userDAO         = new UserDAO();
    private final PropertyDAO     propertyDAO     = new PropertyDAO();
    private final FraudReportDAO  fraudReportDAO  = new FraudReportDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AuditLogDAO     auditLogDAO     = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        // Null-safe check for AJAX notification count endpoint (used by navbar polling)
        if ("notif_count".equals(action)) {
            handleNotifCount(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 1. Aggregate statistics for dashboard cards
        // Note: Accommodates both string representations and structural role 
        // integer mappings (2=Student, 3=Staff, 4=Landlord, 5=Agent)
        // ---------------------------------------------------------------
        int totalStudents   = userDAO.countByRole("STUDENT");
        int totalStaff      = userDAO.countByRole("STAFF");
        int totalLandlords  = userDAO.countByRole("LANDLORD");
        int totalAgents     = userDAO.countByRole("AGENT");
        int totalUsers      = userDAO.countTotal();

        int totalListings   = propertyDAO.countTotal();
        int activeListings  = propertyDAO.countAvailable();
        int pendingListings = propertyDAO.countPending();
        int verifiedListings= propertyDAO.countVerified();

        int openFraudReports = fraudReportDAO.countOpen();

        // ---------------------------------------------------------------
        // 2. Recent activity context tracking maps
        // ---------------------------------------------------------------
        req.setAttribute("recentAuditLogs",   auditLogDAO.findRecent(10, 0));
        req.setAttribute("pendingProperties", propertyDAO.findPending());
        req.setAttribute("popularProperties", propertyDAO.findPopular(5));

        // ---------------------------------------------------------------
        // 3. Forward state to view buffers
        // ---------------------------------------------------------------
        req.setAttribute("totalStudents",    totalStudents);
        req.setAttribute("totalStaff",       totalStaff);
        req.setAttribute("totalLandlords",   totalLandlords);
        req.setAttribute("totalAgents",      totalAgents);
        req.setAttribute("totalUsers",       totalUsers);
        req.setAttribute("totalListings",    totalListings);
        req.setAttribute("activeListings",   activeListings);
        req.setAttribute("pendingListings",  pendingListings);
        req.setAttribute("verifiedListings", verifiedListings);
        req.setAttribute("openFraudReports", openFraudReports);
        req.setAttribute("pageTitle",        "Admin Dashboard");
        req.setAttribute("activePage",       "admin/dashboard");

        req.getRequestDispatcher("/admin/admin-dashboard.jsp")
           .forward(req, resp);
    }

    /**
     * Returns the unread notification count as JSON payload configuration.
     * Called every 60 seconds by main.js for all authenticated users across views.
     */
    private void handleNotifCount(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int userId = SessionUtil.getLoggedInUserId(req);
        int count  = (userId > 0) ? notificationDAO.countUnread(userId) : 0;

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = resp.getWriter()) {
            out.print("{\"count\":" + count + "}");
            out.flush();
        }
    }
}