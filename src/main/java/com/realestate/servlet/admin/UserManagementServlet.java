package com.realestate.servlet.admin;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.UserDAO;
import com.realestate.util.CsrfGuard;
import com.realestate.util.SessionUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * UserManagementServlet - Admin user management (list, search, update status).
 *
 * GET  /admin/users           → List all users (with optional role filter)
 * GET  /admin/users?q=keyword → Search users by name/email
 * POST /admin/users           → Update a user's account status
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/users")
public class UserManagementServlet extends HttpServlet {

    private final UserDAO     userDAO     = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Security Authorization Guard: Verify Administrative Role Privileges
        if (!SessionUtil.isAdmin(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrative access required.");
            return;
        }

        String roleFilter = ValidationUtil.safeTrim(req.getParameter("role"));
        String keyword    = ValidationUtil.safeTrim(req.getParameter("q"));

        // Route lookup execution path dynamically based on criteria
        if (keyword != null && !keyword.isEmpty()) {
            req.setAttribute("users", userDAO.searchUsers(keyword));
            req.setAttribute("searchKeyword", keyword);
        } else {
            String roleArg = (roleFilter == null || roleFilter.isEmpty()) ? null : roleFilter;
            req.setAttribute("users", userDAO.findAll(roleArg));
            req.setAttribute("selectedRole", roleFilter);
        }

        // Hydrate aggregate metric counts for dashboard display components
        req.setAttribute("studentCount",  userDAO.countByRole("STUDENT"));
        req.setAttribute("staffCount",    userDAO.countByRole("STAFF"));
        req.setAttribute("landlordCount", userDAO.countByRole("LANDLORD"));
        req.setAttribute("agentCount",    userDAO.countByRole("AGENT"));
        req.setAttribute("totalCount",    userDAO.countTotal());

        req.setAttribute("pageTitle", "User Management Dashboard");
        req.getRequestDispatcher("/admin/manage-users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // Strict production CSRF protection check
    	if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }

        // Security Authorization Guard: Verify Administrative Role Privileges
        if (!SessionUtil.isAdmin(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrative operations are isolated.");
            return;
        }

        req.setCharacterEncoding("UTF-8");

        int    adminId   = SessionUtil.getLoggedInUserId(req);
        int    targetId  = parseIntSafe(req.getParameter("userId"), 0);
        String newStatus = ValidationUtil.safeTrim(req.getParameter("status"));

        // Validate state mutation arguments
        if (targetId <= 0 || (!"ACTIVE".equals(newStatus) && !"SUSPENDED".equals(newStatus) && !"BLACKLISTED".equals(newStatus))) {
            SessionUtil.setErrorMessage(req, "Invalid user targeting parameters or account status selection.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        // Security Check: Prevent admins from self-modifying, self-suspending, or self-locking
        if (adminId <= 0 || targetId == adminId) {
            SessionUtil.setErrorMessage(req, "Administrative Protection Fault: You cannot modify your own running account status parameters.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        boolean updated = userDAO.updateAccountStatus(targetId, newStatus);

        if (updated) {
            // Write action directly to the system audit stream
            auditLogDAO.log(adminId, "UPDATE_USER_STATUS", "USER", targetId, 
                            "User account #" + targetId + " status shifted to: " + newStatus, 
                            req.getRemoteAddr());
            SessionUtil.setSuccessMessage(req, "Target user profile status shifted to: " + newStatus + " successfully.");
        } else {
            SessionUtil.setErrorMessage(req, "The storage engine rejected the status modification transaction.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users");
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