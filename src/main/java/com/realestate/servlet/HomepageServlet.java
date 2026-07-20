package com.realestate.servlet;

import com.realestate.dao.PropertyDAO;
import com.realestate.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * HomepageServlet - Loads data for the homepage (index.jsp).
 *
 * GET / or GET /home
 *   Loads:
 *     - Featured properties (up to 6)
 *     - Popular properties (up to 4)
 *     - Platform statistics (user/listing counts)
 *
 * This servlet forwards to index.jsp.
 * Direct access to index.jsp still works for backward compatibility.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/home", "/index"})
public class HomepageServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final UserDAO     userDAO     = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ---------------------------------------------------------------
        // 1. Featured properties for hero section
        // ---------------------------------------------------------------
        req.setAttribute("featuredProperties",
                          propertyDAO.findFeatured(6));

        // ---------------------------------------------------------------
        // 2. Popular properties for home section
        // ---------------------------------------------------------------
        req.setAttribute("popularProperties",
                          propertyDAO.findPopular(4));

        // ---------------------------------------------------------------
        // 3. Platform statistics for hero stats bar
        // ---------------------------------------------------------------
        int totalListings  = propertyDAO.countAvailable();
        int totalLandlords = userDAO.countByRole("LANDLORD");
        int totalUsers     = userDAO.countTotal();

        req.setAttribute("heroStatListings",  totalListings);
        req.setAttribute("heroStatLandlords", totalLandlords);
        req.setAttribute("heroStatUsers",     totalUsers);

        // ---------------------------------------------------------------
        // 4. All property types for category grid
        // ---------------------------------------------------------------
        req.setAttribute("allPropertyTypes",
                          propertyDAO.findAllTypes());

        req.setAttribute("pageTitle",
                          "Find Your Perfect Home Near AOPE");

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}