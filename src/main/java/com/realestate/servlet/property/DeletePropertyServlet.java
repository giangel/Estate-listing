package com.realestate.servlet.property;

import com.realestate.dao.AuditLogDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.SessionUtil;
import com.realestate.util.CsrfGuard; // Explicit structural utility import added
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * DeletePropertyServlet - Handles property listing deletion.
 *
 * POST /landlord/delete-property
 * - Verifies ownership or admin role
 * - Deletes property (cascades in DB to images, inquiries, etc.)
 * - Logs audit event
 * - Redirects with flash message
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/landlord/delete-property")
public class DeletePropertyServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Strict production CSRF protection boundary check
        if (!CsrfGuard.isValidToken(req)) {
            CsrfGuard.rejectRequest(req, resp);
            return;
        }
    	
        int currentUserId = SessionUtil.getLoggedInUserId(req);
        int propertyId;

        try {
            String propIdParam = req.getParameter("propertyId");
            if (propIdParam == null || propIdParam.trim().isEmpty()) {
                throw new NumberFormatException("Null or empty parameter string processing exception.");
            }
            propertyId = Integer.parseInt(propIdParam.trim());
        } catch (NumberFormatException e) {
            SessionUtil.setErrorMessage(req, "Invalid property structural reference identifier.");
            resp.sendRedirect(req.getContextPath() + "/landlord/manage-properties");
            return;
        }

        // Verify data context existence mapping
        Property property = propertyDAO.findById(propertyId);
        if (property == null) {
            SessionUtil.setErrorMessage(req, "The requested property context could not be located.");
            resp.sendRedirect(req.getContextPath() + "/landlord/manage-properties");
            return;
        }

        // Security Boundary: Authorization Access Control Level verification
        boolean isAdmin = SessionUtil.isAdmin(req);
        if (property.getOwnerId() != currentUserId && !isAdmin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                           "Access Denied: You do not have permission to delete this property.");
            return;
        }

        // Admin bypass parameters: pass 0 as ownerId to skip matching checks in your custom DAO structures
        int ownerIdForDao = isAdmin ? 0 : currentUserId;
        boolean deleted   = propertyDAO.deleteProperty(propertyId, ownerIdForDao);

        if (deleted) {
            auditLogDAO.log(currentUserId, "DELETE_PROPERTY", "PROPERTY",
                            propertyId,
                            "Property deleted: \"" + property.getTitle() + "\"",
                            req.getRemoteAddr());
            SessionUtil.setSuccessMessage(req,
                "Property \"" + property.getTitle() + "\" deleted successfully.");
        } else {
            SessionUtil.setErrorMessage(req, "The database execution deletion operation failed. Please try again.");
        }

        // Standardized structural redirection matrix boundaries
        if (isAdmin) {
            resp.sendRedirect(req.getContextPath() + "/admin/manage-properties");
        } else {
            resp.sendRedirect(req.getContextPath() + "/landlord/manage-properties");
        }
    }
}