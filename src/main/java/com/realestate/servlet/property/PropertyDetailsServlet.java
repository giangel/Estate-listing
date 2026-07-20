package com.realestate.servlet.property;

import com.realestate.dao.PropertyDAO;
import com.realestate.dao.ReviewDAO;
import com.realestate.model.Property;
import com.realestate.model.Review;
import com.realestate.util.SessionUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * PropertyDetailsServlet - Loads a full property detail page.
 *
 * GET /property?id=N
 * - Loads property with images, amenities, location
 * - Increments view counter
 * - Records in recently_viewed (if logged in)
 * - Loads approved reviews and similar properties
 * - Checks if current user has saved this property
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/property")
public class PropertyDetailsServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final ReviewDAO   reviewDAO   = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ---------------------------------------------------------------
        // 1. Parse and sanitize input parameter property ID
        // ---------------------------------------------------------------
        String idParam = req.getParameter("id");
        int propertyId = 0;
        
        try {
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new NumberFormatException("Null or empty parameter identifier context.");
            }
            propertyId = Integer.parseInt(ValidationUtil.safeTrim(idParam));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested resource structural ID is invalid.");
            return;
        }

        // ---------------------------------------------------------------
        // 2. Load property data structures (including related joins)
        // ---------------------------------------------------------------
        Property property = propertyDAO.findById(propertyId);
        int userId = SessionUtil.getLoggedInUserId(req);
        boolean isAdmin = SessionUtil.isAdmin(req);

        if (property == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Property record could not be found.");
            return;
        }

        // ---------------------------------------------------------------
        // 3. Security Boundary: Visibility Check
        //    (Allows only Admins and the Property Owner to see unapproved/hidden listings)
        // ---------------------------------------------------------------
        boolean isOwner = (userId > 0 && property.getOwnerId() == userId);
        boolean isAvailable = "AVAILABLE".equalsIgnoreCase(property.getPropertyStatus());

        if (!isAvailable && !isAdmin && !isOwner) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This property listing is currently unavailable or pending review.");
            return;
        }

        // ---------------------------------------------------------------
        // 4. Track metric views (Only increment for unique visitors/seekers)
        // ---------------------------------------------------------------
        String ipAddress = req.getRemoteAddr();
        
        // Prevent artificially inflating view counts during landlord previews
        if (!isOwner && !isAdmin) {
            propertyDAO.incrementViewCount(propertyId, userId, ipAddress);
            if (userId > 0) {
                propertyDAO.recordRecentlyViewed(userId, propertyId);
            }
        }

        // ---------------------------------------------------------------
        // 5. Check wishlist interaction status
        // ---------------------------------------------------------------
        boolean isSaved = (userId > 0) && propertyDAO.isSavedByUser(userId, propertyId);

        // ---------------------------------------------------------------
        // 6. Load matching reviews ecosystem metrics
        // ---------------------------------------------------------------
        List<Review> reviews = reviewDAO.findApprovedByProperty(propertyId);
        int userRating = (userId > 0) ? reviewDAO.getUserRating(propertyId, userId) : 0;

        // ---------------------------------------------------------------
        // 7. Load similar categorical recommendations
        // ---------------------------------------------------------------
        List<Property> similarProps = propertyDAO.findSimilar(
            propertyId,
            property.getTypeId(),
            property.getPrice()
        );

        // ---------------------------------------------------------------
        // 8. Bind tracking structures down into the request context buffer
        // ---------------------------------------------------------------
        req.setAttribute("property",      property);
        req.setAttribute("reviews",       reviews);
        req.setAttribute("userRating",    userRating);
        req.setAttribute("similarProps",  similarProps);
        req.setAttribute("isSaved",       isSaved);
        req.setAttribute("pageTitle",     property.getTitle());

        req.getRequestDispatcher("/properties/property-details.jsp").forward(req, resp);
    }
}