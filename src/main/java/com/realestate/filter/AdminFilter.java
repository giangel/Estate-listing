package com.realestate.filter;

import com.realestate.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AdminFilter - Admin Role Guard Filter
 *
 * Runs after AuthFilter on /admin/* routes.
 * Ensures the authenticated user holds the ADMIN role.
 * Non-admin users are redirected to the 403 error page.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!SessionUtil.isAdmin(httpRequest)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Access denied. Administrator privileges required.");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}