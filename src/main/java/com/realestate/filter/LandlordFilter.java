package com.realestate.filter;

import com.realestate.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * LandlordFilter - Landlord Role Guard Filter
 *
 * Runs after AuthFilter on /landlord/* routes.
 * Permits access to LANDLORD, AGENT, and ADMIN roles only.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class LandlordFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!SessionUtil.canCreateListing(httpRequest)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Access denied. Landlord or Agent privileges required.");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}