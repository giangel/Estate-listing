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
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * RegisterServlet - Handles new user registration.
 *
 * GET  /register → Forwards to register.jsp (Generates CSRF token)
 * POST /register → Validates input, creates user, redirects to login
 *
 * Roles supported: STUDENT (2), STAFF (3), LANDLORD (4), AGENT (5)
 * Role-specific profile records are created in the appropriate tables.
 *
 * @author  AOPE CS Department
 * @version 1.4
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO     userDAO     = new UserDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // If already logged in, redirect to appropriate dashboard
        if (SessionUtil.isLoggedIn(req)) {
            redirectToDashboard(req, resp);
            return;
        }
        
        // 1. Generate/Retrieve token within the session context
        String token = CsrfGuard.getOrCreateToken(req);
        
        // 2. Double-down binding: sync both request and session contexts
        req.setAttribute("csrfToken", token);
        req.getSession().setAttribute("csrfToken", token);

        req.setAttribute("pageTitle", "Create Account");
        req.getRequestDispatcher("/auth/register.jsp").forward(req, resp);
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

        // ---------------------------------------------------------------
        // 1. Collect and trim form fields
        // ---------------------------------------------------------------
        String fullName  = ValidationUtil.safeTrim(req.getParameter("fullName"));
        String email     = ValidationUtil.safeTrim(req.getParameter("email"));
        String password  = req.getParameter("password");
        String confirmPw = req.getParameter("confirmPassword");
        String phone     = ValidationUtil.safeTrim(req.getParameter("phone"));
        String roleStr   = ValidationUtil.safeTrim(req.getParameter("role"));
        String gender    = ValidationUtil.safeTrim(req.getParameter("gender"));

        // Role-specific fields
        String matricNumber = ValidationUtil.safeTrim(req.getParameter("matricNumber"));
        String department   = ValidationUtil.safeTrim(req.getParameter("department"));
        String level        = ValidationUtil.safeTrim(req.getParameter("level"));
        String faculty      = ValidationUtil.safeTrim(req.getParameter("faculty"));
        String staffNumber  = ValidationUtil.safeTrim(req.getParameter("staffNumber"));
        String designation  = ValidationUtil.safeTrim(req.getParameter("designation"));
        String businessName = ValidationUtil.safeTrim(req.getParameter("businessName"));
        String agencyName   = ValidationUtil.safeTrim(req.getParameter("agencyName"));

        // ---------------------------------------------------------------
        // 2. Validate required fields
        // ---------------------------------------------------------------
        StringBuilder errors = new StringBuilder();

        if (ValidationUtil.isNullOrEmpty(fullName) || fullName.length() < 3) {
            errors.append("Full name must be at least 3 characters. ");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            errors.append("Please enter a valid email address. ");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            errors.append("Password must be at least 8 characters with uppercase, lowercase, digit, and special character. ");
        }
        if (password == null || !password.equals(confirmPw)) {
            errors.append("Passwords do not match. ");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            errors.append("Please enter a valid Nigerian phone number. ");
        }

        // Parse role
        int roleId = 0;
        try {
            roleId = Integer.parseInt(roleStr);
            if (roleId < 2 || roleId > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errors.append("Please select a valid account type. ");
        }

        // Check for duplicate email execution parameters
        if (errors.length() == 0 && userDAO.emailExists(email)) {
            errors.append("An account with this email address already exists. ");
        }

        // If validation errors exist, return to form with layout state attributes preserved
        if (errors.length() > 0) {
            req.setAttribute("errorMessage", errors.toString().trim());
            
            // Re-seed token back safely across both scopes
            String tokenUpdate = CsrfGuard.getOrCreateToken(req);
            req.setAttribute("csrfToken", tokenUpdate);
            req.getSession().setAttribute("csrfToken", tokenUpdate);
            
            hydrateFormState(req, fullName, email, phone, roleStr, gender, matricNumber, 
                             department, level, faculty, staffNumber, designation, businessName, agencyName);
            req.setAttribute("pageTitle", "Create Account");
            req.getRequestDispatcher("/auth/register.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 3. Create base user record
        // ---------------------------------------------------------------
        String passwordHash = PasswordUtil.hashPassword(password);
        User newUser = new User(roleId, fullName, email, passwordHash, phone);
        int userId = userDAO.createUser(newUser);

        if (userId <= 0) {
            req.setAttribute("errorMessage", "Registration failed inside storage engines. Please try again.");
            String tokenErr = CsrfGuard.getOrCreateToken(req);
            req.setAttribute("csrfToken", tokenErr);
            req.getSession().setAttribute("csrfToken", tokenErr);
            req.getRequestDispatcher("/auth/register.jsp").forward(req, resp);
            return;
        }

        // Create base user profile mapping context
        userDAO.createUserProfile(userId, gender.isEmpty() ? "MALE" : gender);

        // ---------------------------------------------------------------
        // 4. Create role-specific profile transactional units
        // ---------------------------------------------------------------
        boolean profileCreated = createRoleProfile(userId, roleId, matricNumber, department,
                                                   level, faculty, staffNumber, designation,
                                                   businessName, agencyName);

        if (!profileCreated) {
            req.setAttribute("errorMessage", "Error establishing security profile classification context rules.");
            String tokenProfileErr = CsrfGuard.getOrCreateToken(req);
            req.setAttribute("csrfToken", tokenProfileErr);
            req.getSession().setAttribute("csrfToken", tokenProfileErr);
            req.getRequestDispatcher("/auth/register.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 5. Write to transactional audit telemetry
        // ---------------------------------------------------------------
        auditLogDAO.log(userId, "REGISTER", "USER", userId,
                        "New user registered: " + email + " (role_id=" + roleId + ")",
                        req.getRemoteAddr());

        // ---------------------------------------------------------------
        // 6. Context-aware routing redirection to Login Servlet
        // ---------------------------------------------------------------
        SessionUtil.setSuccessMessage(req, "Account created successfully! Please log in.");
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    /**
     * Creates the role-specific profile record after successful registration.
     */
    private boolean createRoleProfile(int userId, int roleId,
                                    String matricNumber, String department,
                                    String level, String faculty,
                                    String staffNumber, String designation,
                                    String businessName, String agencyName) {
        switch (roleId) {
            case 2: // STUDENT
                return insertStudent(userId, matricNumber, department, level, faculty);
            case 3: // STAFF
                return insertStaff(userId, staffNumber, department, designation);
            case 4: // LANDLORD
                return insertLandlord(userId, businessName);
            case 5: // AGENT
                return insertAgent(userId, agencyName);
            default:
                return false;
        }
    }

    private boolean insertStudent(int userId, String matric, String dept, String level, String faculty) {
        String sql = "INSERT INTO students (user_id, matric_number, department, level, faculty) VALUES (?,?,?,?,?)";
        try (Connection c = com.realestate.util.DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, matric.isEmpty() ? null : matric);
            ps.setString(3, dept);
            ps.setString(4, level.isEmpty() ? "ND1" : level);
            ps.setString(5, faculty);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[RegisterServlet.insertStudent] " + e.getMessage());
            return false;
        }
    }

    private boolean insertStaff(int userId, String staffNum, String dept, String designation) {
        String sql = "INSERT INTO staff (user_id, staff_number, department, designation) VALUES (?,?,?,?)";
        try (Connection c = com.realestate.util.DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, staffNum.isEmpty() ? null : staffNum);
            ps.setString(3, dept);
            ps.setString(4, designation);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[RegisterServlet.insertStaff] " + e.getMessage());
            return false;
        }
    }

    private boolean insertLandlord(int userId, String businessName) {
        String sql = "INSERT INTO landlords (user_id, business_name) VALUES (?,?)";
        try (Connection c = com.realestate.util.DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, businessName.isEmpty() ? null : businessName);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[RegisterServlet.insertLandlord] " + e.getMessage());
            return false;
        }
    }

    private boolean insertAgent(int userId, String agencyName) {
        String sql = "INSERT INTO agents (user_id, agency_name) VALUES (?,?)";
        try (Connection c = com.realestate.util.DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, agencyName.isEmpty() ? null : agencyName);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[RegisterServlet.insertAgent] " + e.getMessage());
            return false;
        }
    }

    private void hydrateFormState(HttpServletRequest req, String fullName, String email, String phone, 
                                  String role, String gender, String matric, String dept, String level, 
                                  String faculty, String staffNum, String designation, String bizName, String agencyName) {
        req.setAttribute("fullNameValue", fullName);
        req.setAttribute("emailValue", email);
        req.setAttribute("phoneValue", phone);
        req.setAttribute("roleValue", role);
        req.setAttribute("genderValue", gender);
        req.setAttribute("matricValue", matric);
        req.setAttribute("deptValue", dept);
        req.setAttribute("levelValue", level);
        req.setAttribute("facultyValue", faculty);
        req.setAttribute("staffNumValue", staffNum);
        req.setAttribute("designationValue", designation);
        req.setAttribute("bizNameValue", bizName);
        req.setAttribute("agencyNameValue", agencyName);
    }

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