package com.realestate.servlet.roommate;

import com.realestate.dao.RoommateDAO;
import com.realestate.model.RoommateProfile;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * RoommateMatchServlet - Displays roommate matches for the current student.
 *
 * GET /roommate/matches
 * - Loads the user's profile
 * - Runs the matching algorithm
 * - Forwards matched profiles to the JSP
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/roommate/matches")
public class RoommateMatchServlet extends HttpServlet {

    private final RoommateDAO roommateDAO = new RoommateDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Enforce strict server-side Session Authentication Verification
        int userId = SessionUtil.getLoggedInUserId(req);
        if (userId <= 0) {
            SessionUtil.setErrorMessage(req, "Please sign in to access the roommate matching network.");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=/roommate/matches");
            return;
        }

        // 2. Load the user's own matching preferences profile parameters
        RoommateProfile myProfile = roommateDAO.findProfileByUser(userId);
        List<RoommateProfile> matches;

        // 3. Structured Onboarding routing check 
        // If they don't have a profile or their profile is disabled, route them to set it up first.
        if (myProfile == null) {
            SessionUtil.setInfoMessage(req, "Please complete your roommate preference questionnaire to view your matches.");
            resp.sendRedirect(req.getContextPath() + "/roommate/profile");
            return;
        }

        // 4. Calculate matches based on preference filters if active
        if (myProfile.isActive()) {
            matches = roommateDAO.findMatches(myProfile);
            if (matches == null) {
                matches = Collections.emptyList();
            }
        } else {
            matches = Collections.emptyList();
            SessionUtil.setInfoMessage(req, "Your roommate matching profile status is currently hidden. Turn it back on to browse suggestions.");
        }

        // 5. Hydrate request scope data structures for view rendering
        req.setAttribute("myProfile",  myProfile);
        req.setAttribute("matches",    matches);
        req.setAttribute("pageTitle",  "Find a Roommate");
        
        req.getRequestDispatcher("/roommate/roommate-matches.jsp").forward(req, resp);
    }
}