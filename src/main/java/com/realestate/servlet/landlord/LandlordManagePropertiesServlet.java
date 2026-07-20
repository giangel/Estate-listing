package com.realestate.servlet.landlord;

import com.realestate.dao.PropertyDAO;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * LandlordManagePropertiesServlet - Loads manage-properties page
 * for landlords and agents.
 *
 * GET /landlord/manage-properties
 *   Loads all properties owned by the current user.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/landlord/manage-properties")
public class LandlordManagePropertiesServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int ownerId = SessionUtil.getLoggedInUserId(req);

        req.setAttribute("myProperties",
                          propertyDAO.findByOwner(ownerId));
        req.setAttribute("pageTitle", "Manage Properties");

        req.getRequestDispatcher("/landlord/manage-properties.jsp")
           .forward(req, resp);
    }
}