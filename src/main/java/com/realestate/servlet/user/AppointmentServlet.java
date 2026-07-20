package com.realestate.servlet.user;

import com.realestate.dao.AppointmentDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.Appointment;
import com.realestate.model.Notification;
import com.realestate.model.Property;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * AppointmentServlet - Handles viewing appointment booking and management.
 *
 * GET  /user/appointment       → My Appointments page
 * POST /user/appointment       → Book new viewing appointment
 * POST /user/appointment?action=updateStatus → Owner confirms/cancels
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/appointment")
public class AppointmentServlet extends HttpServlet {

    private final AppointmentDAO  appointmentDAO  = new AppointmentDAO();
    private final PropertyDAO     propertyDAO     = new PropertyDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    userId = SessionUtil.getLoggedInUserId(req);
        String role   = SessionUtil.getLoggedInRole(req);

        if ("LANDLORD".equals(role) || "AGENT".equals(role)) {
            req.setAttribute("appointments", appointmentDAO.findByOwner(userId));
            req.setAttribute("viewMode", "owner");
        } else {
            req.setAttribute("appointments", appointmentDAO.findByRequester(userId));
            req.setAttribute("viewMode", "requester");
        }

        req.setAttribute("pageTitle", "My Appointments");
        req.getRequestDispatcher("/user/my-appointments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // Guard checking the verified token field configuration match
        if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String action = ValidationUtil.safeTrim(req.getParameter("action"));

        if ("updateStatus".equals(action)) {
            handleStatusUpdate(req, resp);
        } else {
            handleNewAppointment(req, resp);
        }
    }

    /**
     * Books a new viewing appointment for a property context.
     */
    private void handleNewAppointment(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    requesterId  = SessionUtil.getLoggedInUserId(req);
        int    propertyId   = parseIntSafe(req.getParameter("propertyId"), 0);
        String dateStr      = ValidationUtil.safeTrim(req.getParameter("preferredDate"));
        String timeStr      = ValidationUtil.safeTrim(req.getParameter("preferredTime"));
        String notes        = ValidationUtil.safeTrim(req.getParameter("notes"));

        if (propertyId <= 0 || ValidationUtil.isNullOrEmpty(dateStr)) {
            req.setAttribute("errorMessage", "Please provide a valid preferred date for the viewing.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        // Parse date matrix safely
        Date preferredDate;
        try {
            LocalDate ld = LocalDate.parse(dateStr);
            if (ld.isBefore(LocalDate.now())) {
                req.setAttribute("errorMessage", "Preferred viewing date cannot be scheduled in the past.");
                forwardToPropertyDetail(req, resp, propertyId);
                return;
            }
            preferredDate = Date.valueOf(ld);
        } catch (DateTimeParseException e) {
            req.setAttribute("errorMessage", "Invalid date value format provided.");
            forwardToPropertyDetail(req, resp, propertyId);
            return;
        }

        // Resilient cross-browser Time parsing architecture (handles HH:mm and HH:mm:ss variations)
        Time preferredTime = null;
        if (!ValidationUtil.isNullOrEmpty(timeStr)) {
            try {
                LocalTime parsedTime;
                if (timeStr.length() == 5) {
                    parsedTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                } else {
                    parsedTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
                }
                preferredTime = Time.valueOf(parsedTime);
            } catch (DateTimeParseException e) {
                System.err.println("[AppointmentServlet] Non-critical time field layout variance encountered: " + timeStr);
            }
        }

        Property property = propertyDAO.findById(propertyId);
        if (property == null) {
            SessionUtil.setErrorMessage(req, "The requested property context could not be located.");
            resp.sendRedirect(req.getContextPath() + "/properties");
            return;
        }

        Appointment appt = new Appointment(
            propertyId, requesterId, property.getOwnerId(),
            preferredDate, preferredTime, notes
        );

        int apptId = appointmentDAO.createAppointment(appt);

        if (apptId > 0) {
            // Send real-time notification downstream to property manager
            Notification notif = new Notification(
                property.getOwnerId(),
                "New Viewing Request",
                "A student has requested to view \"" + property.getTitle() + "\" on " + dateStr,
                "APPOINTMENT", apptId
            );
            notificationDAO.createNotification(notif);

            SessionUtil.setSuccessMessage(req, "Viewing appointment requested successfully! The owner has been notified.");
            resp.sendRedirect(req.getContextPath() + "/property?id=" + propertyId);
        } else {
            req.setAttribute("errorMessage", "Appointment booking transaction engine error. Please try again.");
            forwardToPropertyDetail(req, resp, propertyId);
        }
    }

    /**
     * Handles owner confirming, cancelling, or completing an appointment.
     */
    private void handleStatusUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int    apptId = parseIntSafe(req.getParameter("appointmentId"), 0);
        String status = ValidationUtil.safeTrim(req.getParameter("status"));

        if (apptId <= 0 || ValidationUtil.isNullOrEmpty(status)) {
            SessionUtil.setErrorMessage(req, "Invalid appointment reference identifier.");
            resp.sendRedirect(req.getContextPath() + "/user/appointment");
            return;
        }

        boolean updated = appointmentDAO.updateStatus(apptId, status);
        if (updated) {
            SessionUtil.setSuccessMessage(req, "Appointment status updated to: " + status);
        } else {
            SessionUtil.setErrorMessage(req, "The database state update operation failed.");
        }

        resp.sendRedirect(req.getContextPath() + "/user/appointment");
    }

    /**
     * Internal helper function mapping failure scopes back to layout frames while saving field values.
     */
    private void forwardToPropertyDetail(HttpServletRequest req, HttpServletResponse resp, int propertyId) 
            throws ServletException, IOException {
        // Fetch property representation data maps to support dynamic title renders
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