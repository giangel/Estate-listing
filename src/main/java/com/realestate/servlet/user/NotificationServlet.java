package com.realestate.servlet.user;

import com.realestate.dao.NotificationDAO;
import com.realestate.util.SessionUtil;
import com.realestate.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * NotificationServlet - Handles notification management.
 *
 * GET  /user/notifications
 *   Returns all notifications for current user as JSON.
 *   Used by the notification dropdown in the navbar.
 *
 * POST /user/notifications?action=markAllRead
 *   Marks all notifications as read.
 *
 * POST /user/notifications?action=count
 *   Returns unread count as JSON (for AJAX polling).
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/user/notifications")
public class NotificationServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(req);
        if (userId <= 0) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = ValidationUtil.safeTrim(req.getParameter("action"));

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if ("count".equals(action)) {
            int count = notificationDAO.countUnread(userId);
            out.print("{\"count\":" + count + "}");
            return;
        }

        // Return notifications as JSON
        var notifications = notificationDAO.findByUser(userId);
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < notifications.size(); i++) {
            var n = notifications.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                .append("\"id\":").append(n.getNotificationId()).append(",")
                .append("\"title\":\"").append(escapeJson(n.getTitle())).append("\",")
                .append("\"message\":\"").append(escapeJson(n.getMessage())).append("\",")
                .append("\"type\":\"").append(n.getNotificationType()).append("\",")
                .append("\"isRead\":").append(n.isRead()).append(",")
                .append("\"icon\":\"").append(n.getTypeIcon()).append("\"")
                .append("}");
        }
        json.append("]");
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(req);
        if (userId <= 0) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = ValidationUtil.safeTrim(req.getParameter("action"));

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if ("markAllRead".equals(action)) {
            boolean done = notificationDAO.markAllRead(userId);
            out.print("{\"success\":" + done + "}");
        } else {
            out.print("{\"success\":false,\"error\":\"Unknown action\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}