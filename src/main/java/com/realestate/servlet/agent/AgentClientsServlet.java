package com.realestate.servlet.agent;

import com.realestate.dao.PropertyDAO;
import com.realestate.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * AgentClientsServlet - Loads manage-clients page for agents.
 *
 * GET /agent/manage-clients
 *   Lists all properties managed by this agent.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/agent/manage-clients")
public class AgentClientsServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int agentId = SessionUtil.getLoggedInUserId(req);

        req.setAttribute("agentProperties",
                          propertyDAO.findByOwner(agentId));
        req.setAttribute("pageTitle", "Manage Client Properties");

        req.getRequestDispatcher("/agent/manage-clients.jsp")
           .forward(req, resp);
    }
}