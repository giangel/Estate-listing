package com.realestate.servlet.user;

import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.CsrfGuard;
import com.realestate.util.SessionUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

/**
 * WishlistServlet - Handles saving and removing properties from a user's wishlist.
 *
 * GET  /user/wishlist        → Forwards to saved-properties.jsp
 * POST /user/wishlist        → AJAX toggle (returns JSON response)
 *
 * POST JSON response format:
 * {"status":"saved"}   - property was added to wishlist
 * {"status":"removed"} - property was removed from wishlist
 * {"status":"error"}   - operation failed
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/wishlist")
public class WishlistServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Session Boundary Protection for view loading
        int userId = SessionUtil.getLoggedInUserId(req);
        if (userId <= 0) {
            SessionUtil.setErrorMessage(req, "Please sign in to view your saved properties wishlist.");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=/user/wishlist");
            return;
        }

        // 2. Safely populate listing array collections
        List<Property> savedProperties = propertyDAO.findSavedByUser(userId);
        if (savedProperties == null) {
            savedProperties = Collections.emptyList();
        }

        req.setAttribute("savedProperties", savedProperties);
        req.setAttribute("pageTitle",       "My Saved Properties");
        req.getRequestDispatcher("/user/saved-properties.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // Strict production CSRF boundary check
    	if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }

        int userId = SessionUtil.getLoggedInUserId(req);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Check if an unauthenticated background call hit the endpoint via AJAX
        if (userId <= 0) {
            out.print("{\"status\":\"login_required\"}");
            return;
        }

        String pidStr = ValidationUtil.safeTrim(req.getParameter("propertyId"));
        int propertyId;
        
        try {
            if (pidStr.isEmpty()) {
                throw new NumberFormatException("Null parsing argument parameter tracked.");
            }
            propertyId = Integer.parseInt(pidStr);
        } catch (NumberFormatException e) {
            out.print("{\"status\":\"error\",\"message\":\"Invalid structural property resource tracking index.\"}");
            return;
        }

        // Execute asynchronous state toggle mutation
        String result = propertyDAO.toggleWishlist(userId, propertyId);
        
        if (result == null || "error".equalsIgnoreCase(result)) {
            out.print("{\"status\":\"error\"}");
        } else {
            out.print("{\"status\":\"" + result.toLowerCase() + "\"}");
        }
    }
}