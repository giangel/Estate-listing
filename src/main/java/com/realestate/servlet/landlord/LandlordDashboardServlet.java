package com.realestate.servlet.landlord;

import com.realestate.dao.AppointmentDAO;
import com.realestate.dao.InquiryDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * LandlordDashboardServlet - Loads the landlord dashboard.
 *
 * GET /landlord/dashboard
 *   Loads:
 *     - All properties owned by this landlord
 *     - Counts: total, active, pending listings
 *     - Unread inquiry count
 *     - Pending appointment count
 *     - Recent notifications
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/landlord/dashboard")
public class LandlordDashboardServlet extends HttpServlet {

    private final PropertyDAO     propertyDAO     = new PropertyDAO();
    private final InquiryDAO      inquiryDAO      = new InquiryDAO();
    private final AppointmentDAO  appointmentDAO  = new AppointmentDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int ownerId = SessionUtil.getLoggedInUserId(req);

        // ---------------------------------------------------------------
        // 1. All properties owned by this landlord
        // ---------------------------------------------------------------
        List<Property> myProperties = propertyDAO.findByOwner(ownerId);

        // ---------------------------------------------------------------
        // 2. Compute property status counts
        // ---------------------------------------------------------------
        long totalListings   = myProperties.size();
        long activeListings  = myProperties.stream()
            .filter(p -> "AVAILABLE".equals(p.getPropertyStatus()))
            .count();
        long pendingListings = myProperties.stream()
            .filter(p -> "PENDING".equals(p.getPropertyStatus()))
            .count();

        // ---------------------------------------------------------------
        // 3. Compute total performance metrics
        // ---------------------------------------------------------------
        int totalViews     = myProperties.stream()
            .mapToInt(Property::getViewCount).sum();
        int totalSaves     = myProperties.stream()
            .mapToInt(Property::getSaveCount).sum();
        int totalInquiries = myProperties.stream()
            .mapToInt(Property::getInquiryCount).sum();

        // ---------------------------------------------------------------
        // 4. Unread inquiries count
        // ---------------------------------------------------------------
        int unreadInquiries = inquiryDAO.countUnreadByOwner(ownerId);

        // ---------------------------------------------------------------
        // 5. Recent inquiries (top 5)
        // ---------------------------------------------------------------
        req.setAttribute("recentInquiries",
            inquiryDAO.findByOwner(ownerId)
                      .stream()
                      .limit(5)
                      .collect(java.util.stream.Collectors.toList()));

        // ---------------------------------------------------------------
        // 6. Recent appointments (top 5)
        // ---------------------------------------------------------------
        req.setAttribute("recentAppointments",
            appointmentDAO.findByOwner(ownerId)
                          .stream()
                          .limit(5)
                          .collect(java.util.stream.Collectors.toList()));

        // ---------------------------------------------------------------
        // 7. Notifications
        // ---------------------------------------------------------------
        req.setAttribute("notifications",
                          notificationDAO.findByUser(ownerId));
        notificationDAO.markAllRead(ownerId);

        // ---------------------------------------------------------------
        // 8. Top performing property (by popularity score)
        // ---------------------------------------------------------------
        Property topProperty = myProperties.stream()
            .max(java.util.Comparator.comparingInt(Property::getPopularityScore))
            .orElse(null);

        // ---------------------------------------------------------------
        // 9. Pass everything to JSP
        // ---------------------------------------------------------------
        req.setAttribute("myProperties",    myProperties);
        req.setAttribute("totalListings",   totalListings);
        req.setAttribute("activeListings",  activeListings);
        req.setAttribute("pendingListings", pendingListings);
        req.setAttribute("totalViews",      totalViews);
        req.setAttribute("totalSaves",      totalSaves);
        req.setAttribute("totalInquiries",  totalInquiries);
        req.setAttribute("unreadInquiries", unreadInquiries);
        req.setAttribute("topProperty",     topProperty);
        req.setAttribute("pageTitle",       "Landlord Dashboard");

        req.getRequestDispatcher("/landlord/landlord-dashboard.jsp")
           .forward(req, resp);
    }
}