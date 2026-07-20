package com.realestate.servlet.user;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.InquiryDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.Inquiry;
import com.realestate.model.Notification;
import com.realestate.model.Property;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * InquiryServlet - Handles property inquiry submission and management.
 *
 * GET  /user/inquiry          → My Inquiries page
 * POST /user/inquiry          → Submit new inquiry
 * POST /user/inquiry?action=reply → Owner replies to inquiry
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/inquiry")
public class InquiryServlet extends HttpServlet {

    private final InquiryDAO      inquiryDAO      = new InquiryDAO();
    private final PropertyDAO     propertyDAO     = new PropertyDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AuditLogDAO     auditLogDAO     = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    userId = SessionUtil.getLoggedInUserId(req);
        String role   = SessionUtil.getLoggedInRole(req);

        if ("LANDLORD".equals(role) || "AGENT".equals(role)) {
            // Landlord views inquiries received on their properties
            req.setAttribute("inquiries", inquiryDAO.findByOwner(userId));
            req.setAttribute("viewMode", "owner");
        } else {
            // Student/Staff views their sent inquiries
            req.setAttribute("inquiries", inquiryDAO.findBySender(userId));
            req.setAttribute("viewMode", "sender");
        }

        req.setAttribute("pageTitle", "My Inquiries");
        req.getRequestDispatcher("/user/my-inquiries.jsp").forward(req, resp);
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
        String action = ValidationUtil.safeTrim(req.getParameter("action"));
        int    userId = SessionUtil.getLoggedInUserId(req);

        if ("reply".equals(action)) {
            handleReply(req, resp, userId);
        } else {
            handleNewInquiry(req, resp, userId);
        }
    }

    /**
     * Handles a new inquiry submission from a property seeker.
     */
    private void handleNewInquiry(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   int senderId)
            throws IOException, ServletException {

        int    propertyId = parseIntSafe(req.getParameter("propertyId"), 0);
        String message    = ValidationUtil.safeTrim(req.getParameter("message"));

        // 1. Basic empty check validation boundary
        if (propertyId <= 0 || ValidationUtil.isNullOrEmpty(message)) {
            req.setAttribute("errorMessage", "Please enter your inquiry message before sending.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        // 2. Maximum characters length check boundary
        if (message.length() > 1000) {
            req.setAttribute("errorMessage", "Inquiry message is too long (cannot exceed 1000 characters).");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        Property property = propertyDAO.findById(propertyId);
        if (property == null) {
            SessionUtil.setErrorMessage(req, "The selected property listing could not be found.");
            resp.sendRedirect(req.getContextPath() + "/properties");
            return;
        }

        Inquiry inquiry = new Inquiry(propertyId, senderId, message);
        int inquiryId   = inquiryDAO.createInquiry(inquiry);

        if (inquiryId > 0) {
            // Notify property owner
            Notification notif = new Notification(
                property.getOwnerId(),
                "New Inquiry for \"" + property.getTitle() + "\"",
                "You have received a new inquiry from a property seeker.",
                "INQUIRY", inquiryId
            );
            notificationDAO.createNotification(notif);

            // Audit transaction logging tracking
            auditLogDAO.log(senderId, "SUBMIT_INQUIRY", "PROPERTY",
                            propertyId, "Inquiry submitted for property #" + propertyId, 
                            req.getRemoteAddr());

            SessionUtil.setSuccessMessage(req, "Your inquiry has been sent successfully! The property owner will contact you soon.");
            resp.sendRedirect(req.getContextPath() + "/property?id=" + propertyId);
        } else {
            req.setAttribute("errorMessage", "Inquiry submission failed due to a database storage error. Please try again.");
            forwardToPropertyDetail(req, resp, propertyId);
        }
    }

    /**
     * Handles an owner replying to an existing inquiry.
     */
    private void handleReply(HttpServletRequest req,
                              HttpServletResponse resp,
                              int ownerId)
            throws IOException {

        int    inquiryId = parseIntSafe(req.getParameter("inquiryId"), 0);
        String reply     = ValidationUtil.safeTrim(req.getParameter("reply"));

        if (inquiryId <= 0 || ValidationUtil.isNullOrEmpty(reply)) {
            SessionUtil.setErrorMessage(req, "Reply message content cannot be blank.");
            resp.sendRedirect(req.getContextPath() + "/user/inquiry");
            return;
        }

        boolean success = inquiryDAO.replyToInquiry(inquiryId, reply);
        if (success) {
            SessionUtil.setSuccessMessage(req, "Reply sent successfully.");
        } else {
            SessionUtil.setErrorMessage(req, "Failed to submit reply message tracking mapping.");
        }

        resp.sendRedirect(req.getContextPath() + "/user/inquiry");
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