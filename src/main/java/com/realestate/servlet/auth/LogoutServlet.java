package com.realestate.servlet.auth;

import com.realestate.dao.AuditLogDAO;
import com.realestate.util.CsrfGuard;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * LogoutServlet - Invalidates the user session and redirects to homepage.
 *
 * GET  /logout → Clears session, redirects to home routing mapping
 * POST /logout → Same behaviour (supports strict form-based CSRF protected logout)
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Strict production CSRF protection check for POST requests
        if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }
        processLogout(req, resp);
    }

    /**
     * Executes the secure session destruction pipeline.
     */
    private void processLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int    userId = SessionUtil.getLoggedInUserId(req);
        String role   = SessionUtil.getLoggedInRole(req);

        // 1. Log audit trail events before killing the backing session context
        if (userId > 0) {
            auditLogDAO.log(userId, "LOGOUT", "USER", userId,
                            "User logged out successfully [" + role + "]",
                            req.getRemoteAddr());
        }

        // 2. Clear out container authentication context frames explicitly
        SessionUtil.invalidateSession(req);

        // 3. Setup post-destruction flash notifications safely
        // Force creation of a fresh backing session container object
        req.getSession(true); 
        
        // FIX: Pass 'req' instead of 'freshSession' to match method signature parameters
        SessionUtil.setSuccessMessage(req, "You have been logged out successfully.");

        // 4. Clean context redirection boundaries (Dropping hardcoded raw physical .jsp path hooks)
        resp.sendRedirect(req.getContextPath() + "/");
    }
}