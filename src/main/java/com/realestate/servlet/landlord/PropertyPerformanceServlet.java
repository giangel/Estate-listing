package com.realestate.servlet.landlord;

import com.realestate.dao.InquiryDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * PropertyPerformanceServlet - Loads property analytics for landlords.
 *
 * GET /landlord/property-performance
 *   Loads all owner properties with full performance metrics.
 *
 * GET /landlord/property-performance?id=N
 *   Loads inquiries and stats for a single property.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/landlord/property-performance")
public class PropertyPerformanceServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final InquiryDAO  inquiryDAO  = new InquiryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int ownerId = SessionUtil.getLoggedInUserId(req);

        // Load all properties for this owner
        List<Property> props = propertyDAO.findByOwner(ownerId);

        // Compute aggregate totals
        int totalViews     = props.stream()
            .mapToInt(Property::getViewCount).sum();
        int totalSaves     = props.stream()
            .mapToInt(Property::getSaveCount).sum();
        int totalInquiries = props.stream()
            .mapToInt(Property::getInquiryCount).sum();

        // If a specific property is requested, load its inquiries too
        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                int propertyId = Integer.parseInt(idParam);
                Property selected = propertyDAO.findById(propertyId);
                if (selected != null && selected.getOwnerId() == ownerId) {
                    req.setAttribute("selectedProperty", selected);
                    req.setAttribute("propertyInquiries",
                        inquiryDAO.findByOwner(ownerId)
                                  .stream()
                                  .filter(i -> i.getPropertyId() == propertyId)
                                  .collect(java.util.stream.Collectors.toList()));
                }
            } catch (NumberFormatException ignored) {}
        }

        req.setAttribute("ownerProperties", props);
        req.setAttribute("totalViews",      totalViews);
        req.setAttribute("totalSaves",      totalSaves);
        req.setAttribute("totalInquiries",  totalInquiries);
        req.setAttribute("pageTitle",       "Property Performance");

        req.getRequestDispatcher("/landlord/property-performance.jsp")
           .forward(req, resp);
    }
}