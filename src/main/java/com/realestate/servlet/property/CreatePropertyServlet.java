package com.realestate.servlet.property;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.NotificationDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.*;
import com.realestate.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * CreatePropertyServlet - Handles new property listing creation.
 *
 * GET  /landlord/create-property → Forwards to add-property.jsp
 * POST /landlord/create-property → Validates input, saves property,
 * saves images and amenities, notifies admin
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
@WebServlet("/landlord/create-property")
@MultipartConfig(
    maxFileSize    = 5242880L,   // 5 MB per file
    maxRequestSize = 52428800L,  // 50 MB total
    fileSizeThreshold = 1048576  // 1 MB
)
public class CreatePropertyServlet extends HttpServlet {

    private final PropertyDAO    propertyDAO     = new PropertyDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AuditLogDAO    auditLogDAO     = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        loadFormData(req);
        req.setAttribute("pageTitle", "Add New Property");
        req.getRequestDispatcher("/landlord/add-property.jsp").forward(req, resp);
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
        int ownerId = SessionUtil.getLoggedInUserId(req);

        // ---------------------------------------------------------------
        // 1. Collect form fields
        // ---------------------------------------------------------------
        String title           = ValidationUtil.safeTrim(req.getParameter("title"));
        String description     = ValidationUtil.safeTrim(req.getParameter("description"));
        String priceStr        = ValidationUtil.safeTrim(req.getParameter("price"));
        String typeStr         = req.getParameter("typeId");
        String categoryStr     = req.getParameter("categoryId");
        String campusDistance  = ValidationUtil.safeTrim(req.getParameter("campusDistance"));
        String bedroomsStr     = req.getParameter("bedrooms");
        String bathroomsStr    = req.getParameter("bathrooms");
        String sizeSqmStr      = req.getParameter("sizeSqm");
        String furnishing      = req.getParameter("furnishingStatus");
        String water           = req.getParameter("waterAvailability");
        String electricity     = req.getParameter("electricity");
        String security        = req.getParameter("securityLevel");
        String road            = req.getParameter("roadAccessibility");
        String isFencedStr     = req.getParameter("isFenced");
        String internetStr     = req.getParameter("internetAvailable");
        String areaName        = ValidationUtil.safeTrim(req.getParameter("areaName"));
        String streetAddress   = ValidationUtil.safeTrim(req.getParameter("streetAddress"));
        String landmark        = ValidationUtil.safeTrim(req.getParameter("landmark"));
        String[] amenityIds    = req.getParameterValues("amenities");

        // ---------------------------------------------------------------
        // 2. Validate required fields
        // ---------------------------------------------------------------
        StringBuilder errors = new StringBuilder();

        if (ValidationUtil.isNullOrEmpty(title) || title.length() < 10) {
            errors.append("Property title must be at least 10 characters. ");
        }
        if (ValidationUtil.isNullOrEmpty(description) || description.length() < 30) {
            errors.append("Description must be at least 30 characters. ");
        }
        if (!ValidationUtil.isValidPrice(priceStr)) {
            errors.append("Please enter a valid numeric pricing layout structure. ");
        }
        if (!ValidationUtil.isValidCampusDistance(campusDistance)) {
            errors.append("Please select a clear campus distance value. ");
        }
        if (ValidationUtil.isNullOrEmpty(areaName)) {
            errors.append("Area name identification reference is required. ");
        }

        int typeId     = parseIntSafe(typeStr, 0);
        int categoryId = parseIntSafe(categoryStr, 0);
        if (typeId <= 0)     errors.append("Please select a valid property type configuration. ");
        if (categoryId <= 0) errors.append("Please select a property category layout context. ");

        if (errors.length() > 0) {
            // Setup transactional attributes back to the view context map
            req.setAttribute("errorMessage", errors.toString().trim());
            
            // Retain parameter state data maps natively to allow JSP value extraction mappings
            req.setAttribute("selectedTitle", title);
            req.setAttribute("selectedDescription", description);
            req.setAttribute("selectedPrice", priceStr);
            req.setAttribute("selectedTypeId", typeId);
            req.setAttribute("selectedCategoryId", categoryId);
            req.setAttribute("selectedCampusDistance", campusDistance);
            req.setAttribute("selectedBedrooms", bedroomsStr);
            req.setAttribute("selectedBathrooms", bathroomsStr);
            req.setAttribute("selectedSizeSqm", sizeSqmStr);
            req.setAttribute("selectedFurnishing", furnishing);
            req.setAttribute("selectedWater", water);
            req.setAttribute("selectedElectricity", electricity);
            req.setAttribute("selectedSecurity", security);
            req.setAttribute("selectedRoad", road);
            req.setAttribute("selectedIsFenced", isFencedStr);
            req.setAttribute("selectedInternet", internetStr);
            req.setAttribute("selectedAreaName", areaName);
            req.setAttribute("selectedStreetAddress", streetAddress);
            req.setAttribute("selectedLandmark", landmark);
            req.setAttribute("selectedAmenityIds", amenityIds);

            loadFormData(req);
            req.getRequestDispatcher("/landlord/add-property.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 3. Build Property object map configurations
        // ---------------------------------------------------------------
        Property property = new Property();
        property.setOwnerId(ownerId);
        property.setTypeId(typeId);
        property.setCategoryId(categoryId);
        property.setTitle(title);
        property.setDescription(description);
        property.setPrice(new BigDecimal(priceStr));
        property.setBedrooms(parseIntSafe(bedroomsStr, 1));
        property.setBathrooms(parseIntSafe(bathroomsStr, 1));
        property.setCampusDistance(campusDistance);
        property.setFurnishingStatus(furnishing != null ? furnishing : "UNFURNISHED");
        property.setWaterAvailability(water != null ? water : "UNKNOWN");
        property.setElectricity(electricity != null ? electricity : "UNKNOWN");
        property.setSecurityLevel(security != null ? security : "UNKNOWN");
        property.setRoadAccessibility(road != null ? road : "UNKNOWN");
        property.setFenced("on".equals(isFencedStr) || "true".equals(isFencedStr));
        property.setInternetAvailable("on".equals(internetStr) || "true".equals(internetStr));

        if (!ValidationUtil.isNullOrEmpty(sizeSqmStr)) {
            try { 
                property.setSizeSqm(new BigDecimal(sizeSqmStr)); 
            } catch (Exception ignored) {}
        }

        // ---------------------------------------------------------------
        // 4. Handle image file uploads safely
        // ---------------------------------------------------------------
        String uploadDir = System.getProperty("user.home") + java.io.File.separator + "aope-estate-uploads";
        List<Part> validParts = new ArrayList<>();

        try {
            for (Part part : req.getParts()) {
                if ("images".equals(part.getName()) && part.getSize() > 0) {
                    if (FileUploadUtil.isValidImage(part)) {
                        validParts.add(part);
                    }
                    if (validParts.size() >= 10) break;
                }
            }
        } catch (ServletException ex) {
            System.err.println("[CreatePropertyServlet] Multi-part read configuration error: " + ex.getMessage());
        }

        // ---------------------------------------------------------------
        // 5. Save property to underlying storage structures
        // ---------------------------------------------------------------
        int propertyId = propertyDAO.createProperty(property);

        if (propertyId <= 0) {
            req.setAttribute("errorMessage", "Failed to save property context profile to disk. Please try again.");
            loadFormData(req);
            req.getRequestDispatcher("/landlord/add-property.jsp").forward(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 6. Save image buffers
        // ---------------------------------------------------------------
        String coverImagePath = null;
        int    order          = 0;
        String coverParam     = req.getParameter("coverImageIndex");
        int    coverIndex     = parseIntSafe(coverParam, 0);

        for (int i = 0; i < validParts.size(); i++) {
            try {
                String path = FileUploadUtil.savePropertyImage(validParts.get(i), uploadDir, propertyId);
                boolean isCover = (i == coverIndex);
                if (isCover) coverImagePath = path;

                PropertyImage img = new PropertyImage(propertyId, path, isCover, order++);
                propertyDAO.addImage(img);
            } catch (IOException e) {
                System.err.println("[CreatePropertyServlet] Image compilation dynamic tracking error: " + e.getMessage());
            }
        }

        // Update core background cover path inside the table array mappings
        if (coverImagePath != null) {
            updateCoverImage(propertyId, coverImagePath);
        }

        // ---------------------------------------------------------------
        // 7. Save geographical context location metadata
        // ---------------------------------------------------------------
        PropertyLocation loc = new PropertyLocation(
            propertyId, areaName, streetAddress, landmark, "Ibarapa East", "Oyo State"
        );
        propertyDAO.createLocation(loc);

        // ---------------------------------------------------------------
        // 8. Save matching system amenity intersections
        // ---------------------------------------------------------------
        if (amenityIds != null) {
            for (String aid : amenityIds) {
                try {
                    propertyDAO.addAmenity(propertyId, Integer.parseInt(aid));
                } catch (NumberFormatException ignored) {}
            }
        }

        // ---------------------------------------------------------------
        // 9. Process platform notification dispatch workflows
        // ---------------------------------------------------------------
        notifyAdmins(propertyId, title);

        // ---------------------------------------------------------------
        // 10. Audit trace execution and push permanent redirect view state
        // ---------------------------------------------------------------
        auditLogDAO.log(ownerId, "CREATE_PROPERTY", "PROPERTY", propertyId,
                        "New listing created: \"" + title + "\"",
                        req.getRemoteAddr());

        SessionUtil.setSuccessMessage(req, "Property listing submitted successfully! It will be visible once approved by an administrator.");
        resp.sendRedirect(req.getContextPath() + "/landlord/manage-properties");
    }

    // ---------------------------------------------------------------
    // Auxiliary Data Access Contexts
    // ---------------------------------------------------------------

    private void loadFormData(HttpServletRequest req) throws ServletException, IOException {
        req.setAttribute("propertyTypes",      propertyDAO.findAllTypes());
        req.setAttribute("propertyCategories", propertyDAO.findAllCategories());
        req.setAttribute("amenities",          propertyDAO.findAllAmenities());
    }

    private void updateCoverImage(int propertyId, String imagePath) {
        try (java.sql.Connection c = DBConnection.getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement(
                 "UPDATE properties SET cover_image=? WHERE property_id=?")) {
            ps.setString(1, imagePath);
            ps.setInt(2,    propertyId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[CreatePropertyServlet.updateCoverImage] Exception thrown updating cover asset reference: " + e.getMessage());
        }
    }

    private void notifyAdmins(int propertyId, String title) {
        String sql = "SELECT user_id FROM users WHERE role_id = 1 AND account_status = 'ACTIVE'";
        try (java.sql.Connection c = DBConnection.getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Notification notif = new Notification(
                    rs.getInt("user_id"),
                    "New Property Listing Pending Approval",
                    "A new listing \"" + title + "\" requires your review.",
                    "APPROVAL", propertyId
                );
                notificationDAO.createNotification(notif);
            }
        } catch (Exception e) {
            System.err.println("[CreatePropertyServlet.notifyAdmins] Platform notification routing loop crashed: " + e.getMessage());
        }
    }

    private int parseIntSafe(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try { 
            return Integer.parseInt(val.trim()); 
        } catch (NumberFormatException e) { 
            return def; 
        }
    }

    // FIX: Simply wrap the utility bridge directly instead of extending an un-instantiable utility parent class
    private static final class DBConnection {
        public static java.sql.Connection getConnection() throws java.sql.SQLException {
            return com.realestate.util.DBConnection.getConnection();
        }
    }
}