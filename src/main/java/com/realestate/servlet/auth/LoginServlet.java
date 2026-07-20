package com.realestate.servlet.auth;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.UserDAO;
import com.realestate.model.User;
import com.realestate.util.CsrfGuard;
import com.realestate.util.PasswordUtil;
import com.realestate.util.SessionUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * LoginServlet - Handles user authentication.
 *
 * GET  /login → Forwards to login.jsp
 * POST /login → Authenticates credentials, creates session, redirects
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO     userDAO     = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(req)) {
            redirectToDashboard(req, resp);
            return;
        }
        
        String token = CsrfGuard.getOrCreateToken(req);
        req.setAttribute("csrfToken", token);
        req.getSession().setAttribute("csrfToken", token);
        
        req.setAttribute("pageTitle", "Login");
        req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
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

        String email    = ValidationUtil.safeTrim(req.getParameter("email"));
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        // ---------------------------------------------------------------
        // 1. Basic input validation
        // ---------------------------------------------------------------
        if (ValidationUtil.isNullOrEmpty(email) || ValidationUtil.isNullOrEmpty(password)) {
            req.setAttribute("errorMessage", "Email and password are required.");
            req.setAttribute("emailValue", email);
            req.setAttribute("pageTitle", "Login");
            req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 2. Look up user by email
        // ---------------------------------------------------------------
        User user = userDAO.findByEmail(email);

        if (user == null) {
            req.setAttribute("errorMessage", "No account found with this email address.");
            req.setAttribute("emailValue", email);
            req.setAttribute("pageTitle", "Login");
            req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 3. Verify password
        // ---------------------------------------------------------------
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            auditLogDAO.log(user.getUserId(), "LOGIN_FAILED", "USER",
                            user.getUserId(), "Failed login attempt for: " + email,
                            req.getRemoteAddr());
            req.setAttribute("errorMessage", "Incorrect password. Please try again.");
            req.setAttribute("emailValue", email);
            req.setAttribute("pageTitle", "Login");
            req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 4. Check account status
        // ---------------------------------------------------------------
        if (!"ACTIVE".equals(user.getAccountStatus())) {
            String msg;
            switch (user.getAccountStatus()) {
                case "SUSPENDED":
                    msg = "Your account has been suspended. Please contact the administrator.";
                    break;
                case "BLACKLISTED":
                    msg = "Your account has been permanently banned from the platform.";
                    break;
                default:
                    msg = "Your account is not active. Please contact the administrator.";
            }
            req.setAttribute("errorMessage", msg);
            req.setAttribute("emailValue", email);
            req.setAttribute("pageTitle", "Login");
            req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 5. Mitigate Session Fixation (Renew Session Context Bounds)
        // ---------------------------------------------------------------
        HttpSession oldSession = req.getSession(false);
        String savedToken = null;
        if (oldSession != null) {
            savedToken = (String) oldSession.getAttribute("csrfToken");
            oldSession.invalidate(); // Clear out pre-auth tracking identifiers
        }

        // Build authenticated session ecosystem
        HttpSession newSession = req.getSession(true);
        if (savedToken != null) {
            newSession.setAttribute("csrfToken", savedToken); // Migrate verified token down
        }
        
        SessionUtil.createSession(req, user);
        userDAO.updateLastLogin(user.getUserId());

        // ---------------------------------------------------------------
        // 6. Log successful authentication trace
        // ---------------------------------------------------------------
        auditLogDAO.log(user.getUserId(), "LOGIN", "USER",
                        user.getUserId(),
                        "User logged in successfully: " + email + " [" + user.getRoleName() + "]",
                        req.getRemoteAddr());

        // ---------------------------------------------------------------
        // 7. Safe redirection processing rules
        // ---------------------------------------------------------------
        if (redirect != null && !redirect.isEmpty() && redirect.startsWith("/") && !redirect.contains("..")) {
            resp.sendRedirect(req.getContextPath() + redirect);
        } else {
            redirectToDashboard(req, resp);
        }
    }

    /**
     * Maps roles natively to secure dashboard routing paths instead of plain raw JSP addresses.
     */
    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String role = SessionUtil.getLoggedInRole(req);
        String ctx  = req.getContextPath();
        
        if ("ADMIN".equals(role)) {
            resp.sendRedirect(ctx + "/admin/dashboard");
        } else if ("LANDLORD".equals(role)) {
            resp.sendRedirect(ctx + "/landlord/dashboard");
        } else if ("AGENT".equals(role)) {
            resp.sendRedirect(ctx + "/agent/dashboard");
        } else {
            resp.sendRedirect(ctx + "/user/dashboard");
        }
    }
}