package com.realestate.servlet.property;

import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ComparePropertiesServlet - Loads the property comparison page.
 *
 * GET /properties/compare?ids=1,2,3,4
 *   Loads up to 4 properties identified by the ids parameter.
 *   Properties that do not exist or are not AVAILABLE are skipped.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/properties/compare")
public class ComparePropertiesServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idsParam = req.getParameter("ids");
        List<Property> compareList = new ArrayList<>();

        if (idsParam != null && !idsParam.trim().isEmpty()) {
            String[] idArray = idsParam.split(",");
            for (String idStr : idArray) {
                try {
                    int propertyId = Integer.parseInt(idStr.trim());
                    Property p     = propertyDAO.findById(propertyId);
                    if (p != null) {
                        compareList.add(p);
                    }
                    if (compareList.size() >= 4) break; // max 4
                } catch (NumberFormatException ignored) {}
            }
        }

        req.setAttribute("compareList", compareList);
        req.setAttribute("pageTitle",   "Compare Properties");

        req.getRequestDispatcher("/properties/compare-properties.jsp")
           .forward(req, resp);
    }
}