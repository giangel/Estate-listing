package com.realestate.filter;

import com.realestate.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AgentFilter - Agent Role Guard Filter
 *
 * Runs after AuthFilter on /agent/* routes.
 * Permits access to AGENT and ADMIN roles only.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class AgentFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String role = SessionUtil.getLoggedInRole(httpRequest);
        if (!"AGENT".equals(role) && !"ADMIN".equals(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Access denied. Verified Agent privileges required.");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}