package com.realestate.servlet.admin;

import com.realestate.dao.PropertyDAO;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import com.realestate.model.Property;

/**
 * AdminManagePropertiesServlet - Admin property listing management.
 *
 * GET /admin/manage-properties
 *   Optional ?status=PENDING filters to pending only.
 *   Returns all properties for admin review.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/manage-properties")
public class AdminManagePropertiesServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String statusFilter = ValidationUtil.safeTrim(
            req.getParameter("status"));

        List<Property> properties;

        if ("PENDING".equals(statusFilter)) {
            properties = propertyDAO.findPending();
        } else {
            // Load all available + pending + suspended for admin
            properties = propertyDAO.searchProperties(
                null, 0, 0, null, null, null, "date", 1, 100);
        }

        req.setAttribute("allProperties", properties);
        req.setAttribute("statusFilter",  statusFilter);
        req.setAttribute("pendingCount",  propertyDAO.countPending());
        req.setAttribute("totalCount",    propertyDAO.countTotal());
        req.setAttribute("pageTitle",     "Property Management");

        req.getRequestDispatcher("/admin/manage-properties.jsp")
           .forward(req, resp);
    }
}