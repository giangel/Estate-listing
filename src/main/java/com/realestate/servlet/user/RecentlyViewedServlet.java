package com.realestate.servlet.user;

import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * RecentlyViewedServlet - Displays the user's recently viewed properties.
 *
 * GET /user/recently-viewed
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/recently-viewed")
public class RecentlyViewedServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Enforce strict authentication validation checking
        int userId = SessionUtil.getLoggedInUserId(req);
        if (userId <= 0) {
            // Drop state parameters down and bounce anonymous user to login terminal routing mapping
            SessionUtil.setErrorMessage(req, "Authentication required. Please sign in to view your history.");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=/user/recently-viewed");
            return;
        }

        // 2. Fetch tracked history while enforcing collection integrity boundaries
        List<Property> recentProperties = propertyDAO.findRecentlyViewedByUser(userId);
        if (recentProperties == null) {
            recentProperties = Collections.emptyList();
        }

        // 3. Hydrate request state data attributes for JSP layout parsing
        req.setAttribute("recentProperties", recentProperties);
        req.setAttribute("pageTitle",        "Recently Viewed Listings");
        
        req.getRequestDispatcher("/user/recently-viewed.jsp").forward(req, resp);
    }
}