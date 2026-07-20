package com.realestate.servlet.user;

import com.realestate.dao.UserDAO;
import com.realestate.model.User;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * ProfileServlet - Handles viewing and updating a user's profile.
 *
 * GET  /user/profile → Forwards to profile.jsp with current user data
 * POST /user/profile → Saves updated name, phone, and optional photo
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/profile")
@MultipartConfig(
    maxFileSize       = 2097152L,  // 2 MB
    maxRequestSize    = 2097152L,
    fileSizeThreshold = 1048576
)
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int  userId = SessionUtil.getLoggedInUserId(req);
        User user   = userDAO.findById(userId);

        req.setAttribute("profileUser", user);
        req.setAttribute("pageTitle",   "My Profile");
        req.getRequestDispatcher("/user/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // Strict production CSRF protection boundary check
    	if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }

        req.setCharacterEncoding("UTF-8");
        int userId = SessionUtil.getLoggedInUserId(req);

        String fullName = ValidationUtil.safeTrim(req.getParameter("fullName"));
        String phone    = ValidationUtil.safeTrim(req.getParameter("phone"));

        // ---------------------------------------------------------------
        // 1. Structural Form Fields Validation
        // ---------------------------------------------------------------
        if (ValidationUtil.isNullOrEmpty(fullName) || fullName.length() < 3) {
            req.setAttribute("errorMessage", "Full name must be at least 3 characters.");
            forwardToProfileForm(req, resp, userId);
            return;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            req.setAttribute("errorMessage", "Please enter a valid Nigerian phone number.");
            forwardToProfileForm(req, resp, userId);
            return;
        }

        // ---------------------------------------------------------------
        // 2. Handle Optional Binary Profile Photo Upload Pipeline
        // ---------------------------------------------------------------
        String photoPath = null;
        try {
            Part photoPart = req.getPart("profilePhoto");
            if (photoPart != null && photoPart.getSize() > 0) {
                if (!FileUploadUtil.isValidImage(photoPart)) {
                    req.setAttribute("errorMessage", "Profile photo must be a valid JPEG, PNG, or WebP image layout (max 2 MB).");
                    forwardToProfileForm(req, resp, userId);
                    return;
                }
                String uploadDir = System.getProperty("user.home") + java.io.File.separator + "aope-estate-uploads";
                photoPath = FileUploadUtil.saveProfilePhoto(photoPart, uploadDir, userId);
            }
        } catch (ServletException | IOException e) {
            System.err.println("[ProfileServlet] Critical multi-part parsing boundary error: " + e.getMessage());
            req.setAttribute("errorMessage", "Error parsing upload file metadata parameters.");
            forwardToProfileForm(req, resp, userId);
            return;
        }

        // ---------------------------------------------------------------
        // 3. Database Sync Matrix Execution
        // ---------------------------------------------------------------
        boolean updated = userDAO.updateProfile(userId, fullName, phone, photoPath);

        if (updated) {
            // Hot-reload dynamic state values directly within user session tracker
            User refreshed = userDAO.findById(userId);
            if (refreshed != null) {
                SessionUtil.createSession(req, refreshed);
            }
            SessionUtil.setSuccessMessage(req, "Your user profile data matrix has been successfully optimized.");
            resp.sendRedirect(req.getContextPath() + "/user/profile");
        } else {
            req.setAttribute("errorMessage", "Database persistence layer update rejected. Please try again.");
            forwardToProfileForm(req, resp, userId);
        }
    }

    /**
     * Intercepts failure operations and forwards seamlessly to prevent client parameter state drops.
     */
    private void forwardToProfileForm(HttpServletRequest req, HttpServletResponse resp, int userId) 
            throws ServletException, IOException {
        User user = userDAO.findById(userId);
        req.setAttribute("profileUser", user);
        req.setAttribute("pageTitle",   "My Profile");
        req.getRequestDispatcher("/user/profile.jsp").forward(req, resp);
    }
}