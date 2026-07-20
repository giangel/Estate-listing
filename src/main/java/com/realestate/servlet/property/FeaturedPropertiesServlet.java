package com.realestate.servlet.property;

import com.realestate.dao.PropertyDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * FeaturedPropertiesServlet - Loads the featured properties page.
 *
 * GET /properties/featured
 *   Returns up to 12 featured available properties.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/properties/featured")
public class FeaturedPropertiesServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("featuredProps",
                          propertyDAO.findFeatured(12));
        req.setAttribute("pageTitle", "Featured Properties");

        req.getRequestDispatcher("/properties/featured-properties.jsp")
           .forward(req, resp);
    }
}