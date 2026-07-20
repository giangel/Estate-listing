package com.realestate.servlet.property;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * UpdatePropertyServlet - Handles editing an existing property listing.
 *
 * GET  /landlord/update-property?id=N → Load edit form with existing data
 * POST /landlord/update-property      → Validate and apply updates
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/landlord/update-property")
@MultipartConfig(
    maxFileSize       = 5242880L,     // 5 MB
    maxRequestSize    = 52428800L,    // 50 MB
    fileSizeThreshold = 1048576       // 1 MB
)
public class UpdatePropertyServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int propertyId = parseIntSafe(req.getParameter("id"), 0);
        if (propertyId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/landlord/manage-properties");
            return;
        }

        Property property = propertyDAO.findById(propertyId);
        int ownerId = SessionUtil.getLoggedInUserId(req);

        // Security Boundary Validation: Ensure the editor owns this asset profile (or is an Admin)
        if (property == null || (property.getOwnerId() != ownerId && !SessionUtil.isAdmin(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to edit this property listing.");
            return;
        }

        req.setAttribute("property",           property);
        req.setAttribute("propertyTypes",      propertyDAO.findAllTypes());
        req.setAttribute("propertyCategories", propertyDAO.findAllCategories());
        req.setAttribute("amenities",          propertyDAO.findAllAmenities());
        req.setAttribute("pageTitle",          "Edit Property");
        req.getRequestDispatcher("/landlord/edit-property.jsp").forward(req, resp);
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
        int ownerId    = SessionUtil.getLoggedInUserId(req);
        int propertyId = parseIntSafe(req.getParameter("propertyId"), 0);

        if (propertyId <= 0) {
            SessionUtil.setErrorMessage(req, "Invalid property structural reference identifier.");
            resp.sendRedirect(req.getContextPath() + "/landlord/manage-properties");
            return;
        }

        // Verify entity context ownership matching parameters
        Property existing = propertyDAO.findById(propertyId);
        if (existing == null || (existing.getOwnerId() != ownerId && !SessionUtil.isAdmin(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized modification context context drop error.");
            return;
        }

        // Collect and isolate updated transaction values
        String title       = ValidationUtil.safeTrim(req.getParameter("title"));
        String description = ValidationUtil.safeTrim(req.getParameter("description"));
        String priceStr    = ValidationUtil.safeTrim(req.getParameter("price"));
        String campusDist  = ValidationUtil.safeTrim(req.getParameter("campusDistance"));

        // Validate mandatory input properties
        if (ValidationUtil.isNullOrEmpty(title) || !ValidationUtil.isValidPrice(priceStr) || !ValidationUtil.isValidCampusDistance(campusDist)) {
            req.setAttribute("errorMessage", "Please complete all required fields with structurally valid arguments.");
            forwardToEditForm(req, resp, existing);
            return;
        }

        // Mutate properties map on target data structure
        Property updated = new Property();
        updated.setPropertyId(propertyId);
        updated.setOwnerId(existing.getOwnerId()); // Maintain the original owner ID integrity
        updated.setTypeId(parseIntSafe(req.getParameter("typeId"), existing.getTypeId()));
        updated.setCategoryId(parseIntSafe(req.getParameter("categoryId"), existing.getCategoryId()));
        updated.setTitle(title);
        updated.setDescription(description);
        updated.setPrice(new BigDecimal(priceStr));
        updated.setBedrooms(parseIntSafe(req.getParameter("bedrooms"), 1));
        updated.setBathrooms(parseIntSafe(req.getParameter("bathrooms"), 1));
        updated.setCampusDistance(campusDist);
        updated.setFurnishingStatus(req.getParameter("furnishingStatus") != null ? req.getParameter("furnishingStatus") : "UNFURNISHED");
        updated.setWaterAvailability(req.getParameter("waterAvailability") != null ? req.getParameter("waterAvailability") : "UNKNOWN");
        updated.setElectricity(req.getParameter("electricity") != null ? req.getParameter("electricity") : "UNKNOWN");
        updated.setSecurityLevel(req.getParameter("securityLevel") != null ? req.getParameter("securityLevel") : "UNKNOWN");
        updated.setRoadAccessibility(req.getParameter("roadAccessibility") != null ? req.getParameter("roadAccessibility") : "UNKNOWN");
        updated.setFenced("on".equals(req.getParameter("isFenced")));
        updated.setInternetAvailable("on".equals(req.getParameter("internetAvailable")));

        boolean success = propertyDAO.updateProperty(updated);

        // Child record updates (Execute only if parent commit yields success)
        if (success) {
            propertyDAO.clearAmenities(propertyId);
            String[] amenityIds = req.getParameterValues("amenities");
            if (amenityIds != null) {
                for (String aid : amenityIds) {
                    try { 
                        propertyDAO.addAmenity(propertyId, Integer.parseInt(aid.trim())); 
                    } catch (NumberFormatException ignored) {}
                }
            }

            auditLogDAO.log(ownerId, "UPDATE_PROPERTY", "PROPERTY", propertyId,
                            "Property mapping adjusted successfully: \"" + title + "\"",
                            req.getRemoteAddr());
            SessionUtil.setSuccessMessage(req, "Your property listing metrics have been updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/landlord/manage-properties");
        } else {
            req.setAttribute("errorMessage", "The storage engine rejected the update operation framework.");
            forwardToEditForm(req, resp, existing);
        }
    }

    /**
     * Re-hydrates master lookups and forwards execution to preserve field state on invalidation steps.
     */
    private void forwardToEditForm(HttpServletRequest req, HttpServletResponse resp, Property fallbackData) 
            throws ServletException, IOException {
        req.setAttribute("property",           fallbackData);
        req.setAttribute("propertyTypes",      propertyDAO.findAllTypes());
        req.setAttribute("propertyCategories", propertyDAO.findAllCategories());
        req.setAttribute("amenities",          propertyDAO.findAllAmenities());
        req.setAttribute("pageTitle",          "Edit Property Form Sync Fallback");
        req.getRequestDispatcher("/landlord/edit-property.jsp").forward(req, resp);
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