package com.realestate.servlet.user;

import com.realestate.dao.ReviewDAO;
import com.realestate.util.SessionUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * RatingServlet - Handles AJAX star rating submission.
 *
 * POST /user/rating
 *   Parameters:
 *     propertyId - target property
 *     rating     - integer 1 to 5
 *
 *   Returns JSON: {"success":true,"newAverage":4.2,"totalRatings":15}
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/rating")
public class RatingServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    userId     = SessionUtil.getLoggedInUserId(req);
        String pidStr     = ValidationUtil.safeTrim(
                                req.getParameter("propertyId"));
        String ratingStr  = ValidationUtil.safeTrim(
                                req.getParameter("rating"));

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // Require authentication
        if (userId <= 0) {
            out.print("{\"success\":false,\"error\":\"login_required\"}");
            return;
        }

        // Parse and validate parameters
        int propertyId = 0;
        int ratingVal  = 0;
        try {
            propertyId = Integer.parseInt(pidStr);
            ratingVal  = Integer.parseInt(ratingStr);
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"error\":\"invalid_params\"}");
            return;
        }

        if (propertyId <= 0 || ratingVal < 1 || ratingVal > 5) {
            out.print("{\"success\":false,\"error\":\"invalid_rating\"}");
            return;
        }

        // Save/update rating (upsert - replaces existing rating)
        boolean saved = reviewDAO.saveRating(propertyId, userId, ratingVal);

        if (saved) {
            out.print("{\"success\":true,\"rating\":" + ratingVal + "}");
        } else {
            out.print("{\"success\":false,\"error\":\"save_failed\"}");
        }
    }
}