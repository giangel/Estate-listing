package com.realestate.servlet.admin;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.FraudReportDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.FraudReport;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * FraudManagementServlet - Admin fraud report management + public fraud reporting.
 *
 * GET  /admin/fraud             → Admin view of all fraud reports
 * POST /admin/fraud             → Admin updates report status
 * POST /admin/fraud?action=report → Public/user reports a property
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/fraud")
public class FraudManagementServlet extends HttpServlet {

    private final FraudReportDAO fraudReportDAO = new FraudReportDAO();
    private final PropertyDAO    propertyDAO    = new PropertyDAO();
    private final AuditLogDAO    auditLogDAO    = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String statusFilter = ValidationUtil.safeTrim(req.getParameter("status"));

        req.setAttribute("fraudReports", fraudReportDAO.findAll(statusFilter.isEmpty() ? null : statusFilter));
        req.setAttribute("openCount",    fraudReportDAO.countOpen());
        req.setAttribute("statusFilter", statusFilter);
        req.setAttribute("pageTitle",    "Fraud Reports");
        req.getRequestDispatcher("/admin/fraud-reports.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // Strict production CSRF protection boundary check
    	if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String action = ValidationUtil.safeTrim(req.getParameter("action"));

        if ("report".equals(action)) {
            handleUserReport(req, resp);
        } else {
            handleAdminUpdate(req, resp);
        }
    }

    /**
     * Public-facing fraud report submission (any authenticated user).
     */
    private void handleUserReport(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    reporterId  = SessionUtil.getLoggedInUserId(req);
        int    propertyId  = parseIntSafe(req.getParameter("propertyId"), 0);
        String reason      = ValidationUtil.safeTrim(req.getParameter("reason"));
        String details     = ValidationUtil.safeTrim(req.getParameter("details"));

        // 1. Validation for incomplete fields
        if (propertyId <= 0 || ValidationUtil.isNullOrEmpty(reason) || ValidationUtil.isNullOrEmpty(details)) {
            req.setAttribute("errorMessage", "Please complete all fields in the fraud report form.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        // 2. Length checking
        if (details.length() < 20) {
            req.setAttribute("errorMessage", "Please provide more detail (at least 20 characters) regarding your claim.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        // 3. Duplicate filtering bounds
        if (fraudReportDAO.hasUserReported(reporterId, propertyId)) {
            req.setAttribute("errorMessage", "You have already filed an open investigation report for this listing.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        FraudReport report = new FraudReport(propertyId, reporterId, reason, details);
        boolean saved = fraudReportDAO.createReport(report);

        if (saved) {
            auditLogDAO.log(reporterId, "REPORT_FRAUD", "PROPERTY",
                            propertyId, "Fraud report submitted: " + reason,
                            req.getRemoteAddr());
            SessionUtil.setSuccessMessage(req, "Your fraud report has been submitted. Our team will investigate promptly.");
            resp.sendRedirect(req.getContextPath() + "/property?id=" + propertyId);
        } else {
            req.setAttribute("errorMessage", "Failed to submit report due to an internal system error. Please try again.");
            forwardToPropertyDetail(req, resp, propertyId);
        }
    }

    /**
     * Admin updating the status of a fraud report.
     */
    private void handleAdminUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int    adminId          = SessionUtil.getLoggedInUserId(req);
        int    reportId         = parseIntSafe(req.getParameter("reportId"), 0);
        String status           = ValidationUtil.safeTrim(req.getParameter("status"));
        String resolutionNotes  = ValidationUtil.safeTrim(req.getParameter("resolutionNotes"));

        if (reportId <= 0 || ValidationUtil.isNullOrEmpty(status)) {
            SessionUtil.setErrorMessage(req, "Invalid processing request parameters.");
            resp.sendRedirect(req.getContextPath() + "/admin/fraud");
            return;
        }

        boolean updated = fraudReportDAO.updateStatus(reportId, status, adminId, resolutionNotes);

        if (updated) {
            auditLogDAO.log(adminId, "UPDATE_FRAUD_REPORT", "FRAUD_REPORT",
                            reportId, "Fraud report #" + reportId + " → " + status, 
                            req.getRemoteAddr());
            SessionUtil.setSuccessMessage(req, "Fraud report status updated to: " + status);
        } else {
            SessionUtil.setErrorMessage(req, "The update operation to the storage engine failed.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/fraud");
    }

    /**
     * Internal helper mapping application failures back to layout frames to preserve input states.
     */
    private void forwardToPropertyDetail(HttpServletRequest req, HttpServletResponse resp, int propertyId) 
            throws ServletException, IOException {
        if (propertyId > 0) {
            req.setAttribute("property", propertyDAO.findById(propertyId));
        }
        req.getRequestDispatcher("/property-detail.jsp?id=" + propertyId).forward(req, resp);
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