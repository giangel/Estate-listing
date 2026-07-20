package com.realestate.servlet.user;

import com.realestate.dao.AppointmentDAO;
import com.realestate.dao.InquiryDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * UserDashboardServlet - Loads the student/staff user dashboard.
 *
 * GET /user/dashboard
 *   Loads:
 *     - Unread notification count
 *     - Saved property count
 *     - Sent inquiry count
 *     - Booked appointment count
 *     - Recently viewed properties (last 4)
 *     - Recommended properties (popular listings)
 *
 * GET /user/dashboard?action=notif_count
 *   Returns JSON unread notification count for navbar polling.
 *   (Shared endpoint - used by all authenticated roles)
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/dashboard")
public class UserDashboardServlet extends HttpServlet {

    private final PropertyDAO     propertyDAO     = new PropertyDAO();
    private final InquiryDAO      inquiryDAO      = new InquiryDAO();
    private final AppointmentDAO  appointmentDAO  = new AppointmentDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        // ---------------------------------------------------------------
        // AJAX: Notification count poll (all authenticated users)
        // ---------------------------------------------------------------
        if ("notif_count".equals(action)) {
            int userId = SessionUtil.getLoggedInUserId(req);
            int count  = (userId > 0)
                         ? notificationDAO.countUnread(userId) : 0;
            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.print("{\"count\":" + count + "}");
            return;
        }

        int userId = SessionUtil.getLoggedInUserId(req);

        // ---------------------------------------------------------------
        // 1. Notification count (badge on bell)
        // ---------------------------------------------------------------
        int unreadNotifications = notificationDAO.countUnread(userId);

        // ---------------------------------------------------------------
        // 2. Notifications list (top 5 for dashboard feed)
        // ---------------------------------------------------------------
        req.setAttribute("notifications",
                          notificationDAO.findByUser(userId));

        // ---------------------------------------------------------------
        // 3. Saved properties count
        // ---------------------------------------------------------------
        int savedCount = propertyDAO.findSavedByUser(userId).size();

        // ---------------------------------------------------------------
        // 4. Inquiry count
        // ---------------------------------------------------------------
        int inquiryCount = inquiryDAO.findBySender(userId).size();

        // ---------------------------------------------------------------
        // 5. Appointment count
        // ---------------------------------------------------------------
        int appointmentCount =
            appointmentDAO.findByRequester(userId).size();

        // ---------------------------------------------------------------
        // 6. Recently viewed (last 4 for dashboard preview)
        // ---------------------------------------------------------------
        req.setAttribute("recentProperties",
                          propertyDAO.findRecentlyViewedByUser(userId));

        // ---------------------------------------------------------------
        // 7. Recommended properties (popular for now)
        // ---------------------------------------------------------------
        req.setAttribute("recommendedProperties",
                          propertyDAO.findPopular(4));

        // ---------------------------------------------------------------
        // 8. Mark all notifications as read on dashboard visit
        // ---------------------------------------------------------------
        notificationDAO.markAllRead(userId);

        // ---------------------------------------------------------------
        // 9. Pass stats to JSP
        // ---------------------------------------------------------------
        req.setAttribute("unreadNotifications", unreadNotifications);
        req.setAttribute("savedCount",          savedCount);
        req.setAttribute("inquiryCount",        inquiryCount);
        req.setAttribute("appointmentCount",    appointmentCount);
        req.setAttribute("pageTitle",           "My Dashboard");

        req.getRequestDispatcher("/user/dashboard.jsp").forward(req, resp);
    }
}