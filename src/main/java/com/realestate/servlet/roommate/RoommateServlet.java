package com.realestate.servlet.roommate;

import com.realestate.dao.NotificationDAO;
import com.realestate.dao.RoommateDAO;
import com.realestate.model.Notification;
import com.realestate.model.RoommateProfile;
import com.realestate.model.RoommateRequest;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * RoommateServlet - Manages roommate profile creation and connection requests.
 *
 * GET  /roommate/profile           → Show profile form (create or edit)
 * POST /roommate/profile           → Save/update roommate profile
 * POST /roommate/profile?action=request → Send roommate connection request
 * POST /roommate/profile?action=respond → Accept or decline a request
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/roommate/profile")
public class RoommateServlet extends HttpServlet {

    private final RoommateDAO     roommateDAO     = new RoommateDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(req);
        if (userId <= 0) {
            SessionUtil.setErrorMessage(req, "Please sign in to manage your roommate status parameters.");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=/roommate/profile");
            return;
        }

        // Load existing profile if any
        RoommateProfile existingProfile = roommateDAO.findProfileByUser(userId);

        // Load incoming requests
        List<RoommateRequest> incomingRequests = roommateDAO.findIncomingRequests(userId);

        req.setAttribute("roommateProfile",  existingProfile);
        req.setAttribute("incomingRequests", incomingRequests);
        req.setAttribute("pageTitle", "Roommate Profile");
        req.getRequestDispatcher("/roommate/roommate-profile.jsp").forward(req, resp);
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

        if (userId <= 0) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Active session context missing.");
            return;
        }

        if ("request".equals(action)) {
            handleSendRequest(req, resp, userId);
        } else if ("respond".equals(action)) {
            handleRespond(req, resp, userId);
        } else {
            handleSaveProfile(req, resp, userId);
        }
    }

    /**
     * Saves or updates the current user's roommate profile.
     */
    private void handleSaveProfile(HttpServletRequest req, HttpServletResponse resp, int userId) 
            throws IOException, ServletException {

        String genderPref   = ValidationUtil.safeTrim(req.getParameter("genderPreference"));
        String budgetMinStr = ValidationUtil.safeTrim(req.getParameter("budgetMin"));
        String budgetMaxStr = ValidationUtil.safeTrim(req.getParameter("budgetMax"));
        String area         = ValidationUtil.safeTrim(req.getParameter("preferredArea"));
        String department   = ValidationUtil.safeTrim(req.getParameter("department"));
        String level        = ValidationUtil.safeTrim(req.getParameter("level"));
        String description  = ValidationUtil.safeTrim(req.getParameter("description"));

        BigDecimal minBudget = BigDecimal.ZERO;
        BigDecimal maxBudget = BigDecimal.ZERO;

        // 1. Data Type Validation Matrix
        try { 
            minBudget = new BigDecimal(budgetMinStr.isEmpty() ? "0" : budgetMinStr);
            maxBudget = new BigDecimal(budgetMaxStr.isEmpty() ? "0" : budgetMaxStr);
        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Budget constraints must contain valid numerical values.");
            forwardToProfileForm(req, resp, userId);
            return;
        }

        // 2. Logical Range Integrity Check
        if (minBudget.compareTo(maxBudget) > 0) {
            req.setAttribute("errorMessage", "Minimum budget metric cannot exceed the maximum allowance ceiling.");
            forwardToProfileForm(req, resp, userId);
            return;
        }

        if (ValidationUtil.isNullOrEmpty(area) || ValidationUtil.isNullOrEmpty(description)) {
            req.setAttribute("errorMessage", "Preferred location area and profile summary details cannot be empty.");
            forwardToProfileForm(req, resp, userId);
            return;
        }

        RoommateProfile profile = new RoommateProfile();
        profile.setUserId(userId);
        profile.setGenderPreference(genderPref.isEmpty() ? "ANY" : genderPref);
        profile.setPreferredArea(area);
        profile.setDepartment(department);
        profile.setLevel(level.isEmpty() ? "ND1" : level);
        profile.setDescription(description);
        profile.setBudgetMin(minBudget);
        profile.setBudgetMax(maxBudget);
        profile.setActive(true);

        boolean saved = roommateDAO.saveProfile(profile);
        if (saved) {
            SessionUtil.setSuccessMessage(req, "Roommate preferences saved. Browse matches matching your background constraints!");
            resp.sendRedirect(req.getContextPath() + "/roommate/matches");
        } else {
            req.setAttribute("errorMessage", "Persistence engine rejection. Failed to save profile records.");
            forwardToProfileForm(req, resp, userId);
        }
    }

    /**
     * Sends a roommate connection request to another student.
     */
    private void handleSendRequest(HttpServletRequest req, HttpServletResponse resp, int senderId) 
            throws IOException {

        int    receiverId = parseIntSafe(req.getParameter("receiverId"), 0);
        String message    = ValidationUtil.safeTrim(req.getParameter("message"));

        if (receiverId <= 0 || receiverId == senderId) {
            SessionUtil.setErrorMessage(req, "Invalid target roommate routing transaction identity.");
            resp.sendRedirect(req.getContextPath() + "/roommate/matches");
            return;
        }

        RoommateRequest request = new RoommateRequest(senderId, receiverId, message);
        boolean sent = roommateDAO.sendRequest(request);

        if (sent) {
            // Instantiate clean asynchronous platform notification
            Notification notif = new Notification(
                receiverId,
                "New Roommate Request",
                "A fellow student has sent you a roommate connection request.",
                "ROOMMATE", senderId
            );
            notificationDAO.createNotification(notif);

            SessionUtil.setSuccessMessage(req, "Roommate link request transmitted securely!");
        } else {
            SessionUtil.setErrorMessage(req, "A connection request is already pending or tracking parameters failed.");
        }

        resp.sendRedirect(req.getContextPath() + "/roommate/matches");
    }

    /**
     * Handles accept or decline responses to an incoming roommate request.
     */
    private void handleRespond(HttpServletRequest req, HttpServletResponse resp, int receiverId) 
            throws IOException {

        int    requestId = parseIntSafe(req.getParameter("requestId"), 0);
        String status    = ValidationUtil.safeTrim(req.getParameter("status"));

        if (requestId <= 0 || (!"ACCEPTED".equals(status) && !"DECLINED".equals(status))) {
            SessionUtil.setErrorMessage(req, "Malformed request state modification arguments parameter verification error.");
            resp.sendRedirect(req.getContextPath() + "/roommate/profile");
            return;
        }

        // -----------------------------------------------------------------
        // Explicit Context Authorization Check: 
        // Guarantee the current user matches the actual targeted receiver
        // -----------------------------------------------------------------
        RoommateRequest originalRequest = roommateDAO.findRequestById(requestId);
        if (originalRequest == null || originalRequest.getReceiverId() != receiverId) {
            SessionUtil.setErrorMessage(req, "Unauthorized operation context modification attempt.");
            resp.sendRedirect(req.getContextPath() + "/roommate/profile");
            return;
        }

        boolean updated = roommateDAO.updateRequestStatus(requestId, status);
        if (updated) {
            SessionUtil.setSuccessMessage(req, "Request has been successfully " + status.toLowerCase() + ".");
        } else {
            SessionUtil.setErrorMessage(req, "Data storage tracking layer failed to commit target status modification.");
        }

        resp.sendRedirect(req.getContextPath() + "/roommate/profile");
    }

    /**
     * Re-hydrates state parameters seamlessly on failure to prevent data loss.
     */
    private void forwardToProfileForm(HttpServletRequest req, HttpServletResponse resp, int userId) 
            throws ServletException, IOException {
        req.setAttribute("incomingRequests", roommateDAO.findIncomingRequests(userId));
        req.setAttribute("pageTitle", "Roommate Profile Form Sync Fallback");
        req.getRequestDispatcher("/roommate/roommate-profile.jsp").forward(req, resp);
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