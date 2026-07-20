package com.realestate.servlet.property;

import com.realestate.dao.PropertyDAO;
import com.realestate.model.Property;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * PropertySearchServlet - Handles property browsing, filtering, and AJAX suggestions.
 *
 * GET /properties                    → Full search results page
 * GET /properties?action=suggest&q= → JSON search suggestions for autocomplete
 *
 * Query Parameters:
 * keyword  - text search
 * type     - type_id integer
 * category - category_id integer
 * distance - campus distance enum value
 * minPrice - minimum price
 * maxPrice - maximum price
 * sort     - price_asc | price_desc | date | popular | rating
 * page     - page number (default 1)
 * featured - if "true", returns featured properties only
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/properties")
public class PropertySearchServlet extends HttpServlet {

    private static final int PAGE_SIZE = 12;
    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = ValidationUtil.safeTrim(req.getParameter("action"));

        // Route AJAX autocomplete requests
        if ("suggest".equals(action)) {
            handleSuggestions(req, resp);
            return;
        }

        // ---------------------------------------------------------------
        // 1. Parse and sanitize filter parameters
        // ---------------------------------------------------------------
        String keyword  = ValidationUtil.safeTrim(req.getParameter("keyword"));
        String distParm = ValidationUtil.safeTrim(req.getParameter("distance"));
        String sortBy   = ValidationUtil.safeTrim(req.getParameter("sort"));
        String featured = req.getParameter("featured");

        int typeId     = parseIntSafe(req.getParameter("type"),     0);
        int categoryId = parseIntSafe(req.getParameter("category"), 0);
        int page       = parseIntSafe(req.getParameter("page"),     1);
        if (page < 1) page = 1;

        BigDecimal minPrice = parsePriceSafe(req.getParameter("minPrice"));
        BigDecimal maxPrice = parsePriceSafe(req.getParameter("maxPrice"));

        // Validate campus distance constraints
        String campusDistance = ValidationUtil.isValidCampusDistance(distParm) ? distParm : null;

        // ---------------------------------------------------------------
        // 2. Execute search queries against the storage layer
        // ---------------------------------------------------------------
        List<Property> properties;
        int totalResults;

        if ("true".equals(featured)) {
            properties   = propertyDAO.findFeatured(PAGE_SIZE);
            totalResults = (properties != null) ? properties.size() : 0;
        } else {
            properties   = propertyDAO.searchProperties(
                keyword, typeId, categoryId, campusDistance,
                minPrice, maxPrice, sortBy, page, PAGE_SIZE
            );
            totalResults = propertyDAO.countSearchResults(
                keyword, typeId, categoryId, campusDistance, minPrice, maxPrice
            );
        }

        // ---------------------------------------------------------------
        // 3. Dynamic pagination boundary calculations
        // ---------------------------------------------------------------
        int totalPages = (totalResults == 0) ? 1 : (int) Math.ceil((double) totalResults / PAGE_SIZE);
        
        // Prevent out-of-bounds page requests
        if (page > totalPages) {
            page = totalPages;
        }

        // ---------------------------------------------------------------
        // 4. Load metadata lookups for the filter sidebar components
        // ---------------------------------------------------------------
        req.setAttribute("propertyTypes",       propertyDAO.findAllTypes());
        req.setAttribute("propertyCategories",  propertyDAO.findAllCategories());
        req.setAttribute("amenities",           propertyDAO.findAllAmenities());

        // ---------------------------------------------------------------
        // 5. Hydrate request attributes for JSP compilation
        // ---------------------------------------------------------------
        req.setAttribute("properties",    properties);
        req.setAttribute("totalResults",  totalResults);
        req.setAttribute("currentPage",   page);
        req.setAttribute("totalPages",    totalPages);
        req.setAttribute("keyword",       keyword);
        req.setAttribute("selectedType",  typeId);
        req.setAttribute("selectedCat",   categoryId);
        req.setAttribute("selectedDist",  campusDistance);
        req.setAttribute("minPrice",      minPrice);
        req.setAttribute("maxPrice",      maxPrice);
        req.setAttribute("sortBy",        sortBy);
        req.setAttribute("isFeatured",    "true".equals(featured));
        req.setAttribute("pageTitle",     "Browse Properties");

        req.getRequestDispatcher("/properties/browse-properties.jsp").forward(req, resp);
    }

    /**
     * Serves structured JSON suggestions for async autocomplete components.
     * Output schema pattern: [{"id":1,"title":"...","area":"..."}, ...]
     */
    private void handleSuggestions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String q = ValidationUtil.safeTrim(req.getParameter("q"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Prevent heavy query operations on tiny input fragments
        if (q.length() < 2) {
            out.print("[]");
            return;
        }

        List<Property> suggestions = propertyDAO.getSuggestions(q);
        
        if (suggestions == null || suggestions.isEmpty()) {
            out.print("[]");
            return;
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < suggestions.size(); i++) {
            Property p = suggestions.get(i);
            if (i > 0) {
                json.append(",");
            }
            
            String areaName = (p.getLocation() != null) ? p.getLocation().getAreaName() : "";
            
            json.append("{")
                .append("\"id\":").append(p.getPropertyId()).append(",")
                .append("\"title\":\"").append(escapeJson(p.getTitle())).append("\",")
                .append("\"area\":\"").append(escapeJson(areaName)).append("\"")
                .append("}");
        }
        json.append("]");
        out.print(json.toString());
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private int parseIntSafe(String value, int defaultVal) {
        if (value == null || value.trim().isEmpty()) return defaultVal;
        try { 
            return Integer.parseInt(value.trim()); 
        } catch (NumberFormatException e) { 
            return defaultVal; 
        }
    }

    private BigDecimal parsePriceSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { 
            return new BigDecimal(value.trim()); 
        } catch (Exception e) { 
            return null; 
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    // Avoid structural character breaks on ASCII controls
                    if (ch >= 0 && ch <= 31) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u").append("0".repeat(4 - ss.length())).append(ss);
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}