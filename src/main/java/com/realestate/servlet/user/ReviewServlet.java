package com.realestate.servlet.user;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.dao.ReviewDAO;
import com.realestate.model.Review;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * ReviewServlet - Handles property review and rating submissions.
 *
 * POST /user/review               → Submit new property review
 * POST /user/review?action=rate   → Submit/update star rating only
 * POST /user/review?action=moderate (admin) → Approve or reject a review
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/review")
public class ReviewServlet extends HttpServlet {

    private final ReviewDAO   reviewDAO   = new ReviewDAO();
    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

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
        int    userId = SessionUtil.getLoggedInUserId(req);

        if ("rate".equals(action)) {
            handleRating(req, resp, userId);
        } else if ("moderate".equals(action)) {
            // Explicit Security Authorization Layer Isolation Check
            if (SessionUtil.isAdmin(req)) {
                handleModeration(req, resp, userId);
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrative rights required.");
            }
        } else {
            handleNewReview(req, resp, userId);
        }
    }

    private void handleNewReview(HttpServletRequest req,
                                  HttpServletResponse resp,
                                  int userId) throws IOException, ServletException {

        int    propertyId   = parseIntSafe(req.getParameter("propertyId"), 0);
        String reviewTitle  = ValidationUtil.safeTrim(req.getParameter("reviewTitle"));
        String reviewBody   = ValidationUtil.safeTrim(req.getParameter("reviewBody"));
        String ratingStr    = req.getParameter("rating");

        // 1. Validation for incomplete fields
        if (propertyId <= 0 || ValidationUtil.isNullOrEmpty(reviewTitle) || ValidationUtil.isNullOrEmpty(reviewBody)) {
            req.setAttribute("errorMessage", "Please complete all review fields.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        // 2. Length checking
        if (reviewBody.length() < 20) {
            req.setAttribute("errorMessage", "Review body must be at least 20 characters long.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        // Save review
        Review review = new Review(propertyId, userId, reviewTitle, reviewBody);
        int reviewId  = reviewDAO.createReview(review);

        // Save optional rating metrics parameters safely
        if (ratingStr != null && !ratingStr.trim().isEmpty()) {
            try {
                int ratingVal = Integer.parseInt(ratingStr.trim());
                if (ratingVal >= 1 && ratingVal <= 5) {
                    reviewDAO.saveRating(propertyId, userId, ratingVal);
                }
            } catch (NumberFormatException ignored) {}
        }

        if (reviewId > 0) {
            auditLogDAO.log(userId, "SUBMIT_REVIEW", "PROPERTY",
                            propertyId, "Review submitted for property #" + propertyId, 
                            req.getRemoteAddr());
            SessionUtil.setSuccessMessage(req, "Review submitted successfully! It will appear after admin approval.");
            resp.sendRedirect(req.getContextPath() + "/property?id=" + propertyId);
        } else {
            req.setAttribute("errorMessage", "Failed to submit review due to an internal persistence error.");
            forwardToPropertyDetail(req, resp, propertyId);
        }
    }

    private void handleRating(HttpServletRequest req,
                               HttpServletResponse resp,
                               int userId) throws IOException {

        int propertyId = parseIntSafe(req.getParameter("propertyId"), 0);
        int ratingVal  = parseIntSafe(req.getParameter("rating"), 0);

        if (propertyId <= 0 || ratingVal < 1 || ratingVal > 5) {
            SessionUtil.setErrorMessage(req, "Invalid rating parameters provided.");
            resp.sendRedirect(req.getContextPath() + "/property?id=" + propertyId);
            return;
        }

        reviewDAO.saveRating(propertyId, userId, ratingVal);
        SessionUtil.setSuccessMessage(req, "Rating saved successfully.");
        resp.sendRedirect(req.getContextPath() + "/property?id=" + propertyId);
    }

    private void handleModeration(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   int adminId) throws IOException {

        int    reviewId = parseIntSafe(req.getParameter("reviewId"), 0);
        String status   = ValidationUtil.safeTrim(req.getParameter("status"));

        if (reviewId <= 0 || (!"APPROVED".equals(status) && !"REJECTED".equals(status))) {
            SessionUtil.setErrorMessage(req, "Invalid moderation processing parameters.");
            resp.sendRedirect(req.getContextPath() + "/admin/manage-properties");
            return;
        }

        boolean updated = reviewDAO.updateStatus(reviewId, status);
        if (updated) {
            auditLogDAO.log(adminId, "MODERATE_REVIEW", "REVIEW",
                            reviewId, "Review #" + reviewId + " marked as " + status.toLowerCase(),
                            req.getRemoteAddr());
            SessionUtil.setSuccessMessage(req, "Review moderated to status: " + status.toLowerCase());
        } else {
            SessionUtil.setErrorMessage(req, "The moderation transition state modification update failed.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/manage-properties");
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