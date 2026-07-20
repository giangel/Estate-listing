package com.realestate.servlet.admin;

import com.realestate.dao.AuditLogDAO;
import com.realestate.model.AuditLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * AuditLogServlet - Admin audit log viewer with pagination.
 *
 * GET /admin/audit-logs
 *   Optional ?page=N for pagination (20 per page).
 *   Optional ?action=LOGIN to filter by action type.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/admin/audit-logs")
public class AuditLogServlet extends HttpServlet {

    private static final int PAGE_SIZE    = 20;
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Parse page number
        int page = 1;
        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null) page = Integer.parseInt(pageParam);
            if (page < 1)         page = 1;
        } catch (NumberFormatException ignored) {}

        // Parse action filter
        String actionFilter = req.getParameter("action");
        int    offset       = (page - 1) * PAGE_SIZE;

        // Load logs
        List<AuditLog> logs;
        if (actionFilter != null && !actionFilter.trim().isEmpty()) {
            logs = auditLogDAO.findFiltered(actionFilter.trim(), 0, PAGE_SIZE);
        } else {
            logs = auditLogDAO.findRecent(PAGE_SIZE, offset);
        }

        // Pagination
        int totalLogs  = auditLogDAO.countTotal();
        int totalPages = (int) Math.ceil((double) totalLogs / PAGE_SIZE);

        req.setAttribute("logs",         logs);
        req.setAttribute("totalLogs",    totalLogs);
        req.setAttribute("currentPage",  page);
        req.setAttribute("totalPages",   totalPages);
        req.setAttribute("actionFilter", actionFilter);
        req.setAttribute("pageTitle",    "Audit Logs");

        req.getRequestDispatcher("/admin/audit-logs.jsp").forward(req, resp);
    }
}