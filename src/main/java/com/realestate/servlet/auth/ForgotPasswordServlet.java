package com.realestate.servlet.auth;

import com.realestate.dao.UserDAO;
import com.realestate.model.User;
import com.realestate.util.CsrfGuard;
import com.realestate.util.EmailUtil;
import com.realestate.util.PasswordUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * ForgotPasswordServlet - Handles password reset request and token-based reset.
 *
 * GET  /forgot-password               → Show email request form
 * POST /forgot-password (action=request) → Generate token, simulate email
 * GET  /forgot-password?action=reset&token=XXX → Show reset form
 * POST /forgot-password (action=reset)   → Validate token, update password
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

   
        
    	  // Generate CSRF token before JSP renders
        String csrfToken = CsrfGuard.getOrCreateToken(req);
        req.setAttribute("csrfToken", csrfToken);
        req.getSession().setAttribute("csrfToken", csrfToken);

        String action     = req.getParameter("action");
        String resetToken = req.getParameter("token");  

        if ("reset".equals(action) && resetToken != null && !resetToken.isEmpty()) {
            User user = userDAO.findByResetToken(resetToken);
            if (user == null) {
                req.setAttribute("errorMessage",
                    "This reset link is invalid or has expired. Please request a new one.");
                req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
                return;
            }
            req.setAttribute("token", resetToken);
            req.setAttribute("pageTitle", "Reset Password");
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
        } else {
            req.setAttribute("pageTitle", "Forgot Password");
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
        }
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

        if ("request".equals(action)) {
            handleResetRequest(req, resp);
        } else if ("reset".equals(action)) {
            handlePasswordReset(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
        }
    }

    /**
     * Handles step 1: user submits their email to request a reset link.
     */
    private void handleResetRequest(HttpServletRequest req,
                                     HttpServletResponse resp)
            throws ServletException, IOException {

        String email = ValidationUtil.safeTrim(req.getParameter("email"));

        if (!ValidationUtil.isValidEmail(email)) {
            req.setAttribute("errorMessage", "Please enter a valid email address.");
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        User user = userDAO.findByEmail(email);

        // Security Validation Boundary: always present success state 
        // to mitigate malicious email enumeration attacks.
        if (user != null && "ACTIVE".equals(user.getAccountStatus())) {
            String token = PasswordUtil.generateResetToken();
            userDAO.saveResetToken(user.getUserId(), token, 60); // 60 minutes expiry

            String baseUrl = req.getScheme() + "://" + req.getServerName() +
                             ":" + req.getServerPort() + req.getContextPath();
            EmailUtil.sendPasswordResetEmail(email, token, baseUrl);
        }

        req.setAttribute("successMessage",
            "If an account with that email exists, a reset link has been sent. " +
            "Please check your email (check the server console in development mode).");
        req.setAttribute("pageTitle", "Forgot Password");
        req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
    }

    /**
     * Handles step 2: user submits new password using the reset token.
     */
    private void handlePasswordReset(HttpServletRequest req,
                                      HttpServletResponse resp)
            throws ServletException, IOException {

        String token     = ValidationUtil.safeTrim(req.getParameter("token"));
        String newPw     = req.getParameter("newPassword");
        String confirmPw = req.getParameter("confirmPassword");

        // Validate token integrity against database layer mapping
        User user = userDAO.findByResetToken(token);
        if (user == null) {
            req.setAttribute("errorMessage", "This reset link is invalid or has expired.");
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        // Validate password complexity rules
        if (!ValidationUtil.isValidPassword(newPw)) {
            req.setAttribute("errorMessage",
                "Password must be at least 8 characters with uppercase, lowercase, digit, and a special character.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        if (!newPw.equals(confirmPw)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        // Hash cleartext values using secure cryptography schemas
        String newHash  = PasswordUtil.hashPassword(newPw);
        boolean updated = userDAO.updatePassword(user.getUserId(), newHash);

        if (updated) {
            // Push dynamic confirmation state notification down to session mapping context
            HttpSession session = req.getSession(true);
            session.setAttribute("successMessage", "Password reset successful! Please log in with your new credentials.");
            
            // Standardized application routing endpoint reference
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("errorMessage", "Password reset processing failure. Please try again.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
        }
    }
}