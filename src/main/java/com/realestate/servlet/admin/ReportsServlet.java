package com.realestate.servlet.admin;

import com.realestate.dao.FraudReportDAO;
import com.realestate.dao.PropertyDAO;
import com.realestate.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * ReportsServlet - Admin analytics and report generation.
 *
 * GET /admin/reports
 *   Aggregates user and property statistics for the reports page.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/reports")
public class ReportsServlet extends HttpServlet {

    private final UserDAO        userDAO        = new UserDAO();
    private final PropertyDAO    propertyDAO    = new PropertyDAO();
    private final FraudReportDAO fraudReportDAO = new FraudReportDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // User statistics
        req.setAttribute("rptStudents",  userDAO.countByRole("STUDENT"));
        req.setAttribute("rptStaff",     userDAO.countByRole("STAFF"));
        req.setAttribute("rptLandlords", userDAO.countByRole("LANDLORD"));
        req.setAttribute("rptAgents",    userDAO.countByRole("AGENT"));
        req.setAttribute("rptTotal",     userDAO.countTotal());

        // Property statistics
        req.setAttribute("rptListings",  propertyDAO.countTotal());
        req.setAttribute("rptAvailable", propertyDAO.countAvailable());
        req.setAttribute("rptPending",   propertyDAO.countPending());
        req.setAttribute("rptVerified",  propertyDAO.countVerified());

        // Fraud statistics
        req.setAttribute("rptFraudOpen", fraudReportDAO.countOpen());

        // Top 10 popular properties
        req.setAttribute("rptPopular",   propertyDAO.findPopular(10));

        req.setAttribute("pageTitle", "Reports & Analytics");

        req.getRequestDispatcher("/admin/reports.jsp").forward(req, resp);
    }
}