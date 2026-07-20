package com.realestate.servlet.property;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.dao.UserDAO;
import com.realestate.model.Notification;
import com.realestate.model.Property;
import com.realestate.model.User;
import com.realestate.util.CsrfGuard;
import com.realestate.util.EmailUtil;
import com.realestate.util.SessionUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * PropertyApprovalServlet - Admin-only: approve or reject a pending listing.
 *
 * POST /admin/approve-property
 * Parameters:
 * propertyId - target property
 * action     - "approve" or "reject"
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/approve-property")
public class PropertyApprovalServlet extends HttpServlet {

    private final PropertyDAO     propertyDAO     = new PropertyDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO         userDAO         = new UserDAO();
    private final AuditLogDAO     auditLogDAO     = new AuditLogDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // Strict production CSRF protection check
    	if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }

        // Security Boundary: Force strict server-side Role Authorization Verification
        if (!SessionUtil.isAdmin(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, 
                           "Access Denied: Administrative privileges are required to perform this action.");
            return;
        }

        int adminId = SessionUtil.getLoggedInUserId(req);
        int propertyId;
        String action = ValidationUtil.safeTrim(req.getParameter("action"));

        try {
            String propIdParam = req.getParameter("propertyId");
            if (propIdParam == null || propIdParam.trim().isEmpty()) {
                throw new NumberFormatException("Null or empty property parameter tracking error.");
            }
            propertyId = Integer.parseInt(propIdParam.trim());
        } catch (NumberFormatException e) {
            SessionUtil.setErrorMessage(req, "Invalid property structural reference identifier.");
            resp.sendRedirect(req.getContextPath() + "/admin/manage-properties");
            return;
        }

        Property property = propertyDAO.findById(propertyId);
        if (property == null) {
            SessionUtil.setErrorMessage(req, "The target property listing could not be found.");
            resp.sendRedirect(req.getContextPath() + "/admin/manage-properties");
            return;
        }

        boolean success;
        String  auditAction;
        String  notifTitle;
        String  notifMessage;

        if ("approve".equalsIgnoreCase(action)) {
            success      = propertyDAO.approveProperty(propertyId, adminId);
            auditAction  = "APPROVE_PROPERTY";
            notifTitle   = "Your Listing Has Been Approved";
            notifMessage = "Your property \"" + property.getTitle() + "\" is now live and visible to property seekers.";
        } else if ("reject".equalsIgnoreCase(action)) {
            success      = propertyDAO.rejectProperty(propertyId, adminId);
            auditAction  = "REJECT_PROPERTY";
            notifTitle   = "Your Listing Was Not Approved";
            notifMessage = "Your property \"" + property.getTitle() + "\" did not pass our review. Please update it and resubmit.";
        } else {
            SessionUtil.setErrorMessage(req, "Unknown workflow administrative action parameter requested.");
            resp.sendRedirect(req.getContextPath() + "/admin/manage-properties");
            return;
        }

        if (success) {
            // Persist platform internal notification workflow data mapping
            Notification notif = new Notification(
                property.getOwnerId(), notifTitle, notifMessage, "APPROVAL", propertyId
            );
            notificationDAO.createNotification(notif);

            // Fetch landlord profile data downstream if the base object payload lacks joined entries
            String ownerEmail = property.getOwnerEmail();
            String ownerName  = property.getOwnerName();

            if (ValidationUtil.isNullOrEmpty(ownerEmail) || ValidationUtil.isNullOrEmpty(ownerName)) {
                User landlord = userDAO.findById(property.getOwnerId());
                if (landlord != null) {
                    ownerEmail = landlord.getEmail();
                    ownerName  = landlord.getFullName();
                }
            }

            // Dispatch external mail confirmation upon successful listing approval
            if (!ValidationUtil.isNullOrEmpty(ownerEmail) && "approve".equalsIgnoreCase(action)) {
                String baseUrl = req.getScheme() + "://" + req.getServerName() +
                                 ":" + req.getServerPort() + req.getContextPath();
                EmailUtil.sendPropertyApprovedEmail(
                    ownerEmail, ownerName, property.getTitle(), propertyId, baseUrl
                );
            }

            // Write transactional audit telemetry
            auditLogDAO.log(adminId, auditAction, "PROPERTY", propertyId,
                            "Admin " + auditAction.toLowerCase().replace("_", " ") + ": \"" + property.getTitle() + "\"",
                            req.getRemoteAddr());

            SessionUtil.setSuccessMessage(req,
                "Property \"" + property.getTitle() + "\" has been successfully " +
                ("approve".equalsIgnoreCase(action) ? "approved." : "rejected."));
        } else {
            SessionUtil.setErrorMessage(req, "The review transition state update failed inside the database engine.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/manage-properties");
    }
}