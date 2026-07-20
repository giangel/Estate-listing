package com.realestate.filter;

import com.realestate.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthFilter - Authentication Guard Filter
 *
 * Intercepts requests to protected URL patterns and verifies
 * that a valid user session exists. Unauthenticated requests
 * are redirected to the login page with the original URL
 * preserved as a redirect parameter.
 *
 * Protected URL patterns (configured in web.xml):
 * /user/*
 * /landlord/*
 * /agent/*
 * /admin/*
 * /roommate/*
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[AuthFilter] Initialized - protecting authenticated routes.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Safely extract the relative path path after the context root (/estate)
        String path = requestURI.substring(contextPath.length());

        // ---------------------------------------------------------------
        // 1. CRITICAL ASSET BYPASS
        //    If the request is for any static asset (CSS, JS, images), 
        //    pass it straight down the chain without executing session guards.
        // ---------------------------------------------------------------
        if (path.startsWith("/assets/") || path.startsWith("/static/") || 
            path.endsWith(".css") || path.endsWith(".js") || 
            path.endsWith(".png") || path.endsWith(".jpg") || 
            path.endsWith(".jpeg") || path.endsWith(".gif") || path.endsWith(".svg")) {
            
            chain.doFilter(request, response);
            return;
        }

        // ---------------------------------------------------------------
        // 2. Check for a valid authenticated session
        // ---------------------------------------------------------------
        if (!SessionUtil.isLoggedIn(httpRequest)) {
            // Capture the originally requested URL to redirect back after login
            String requestedUrl = httpRequest.getRequestURI();
            String queryString  = httpRequest.getQueryString();
            if (queryString != null) {
                requestedUrl += "?" + queryString;
            }

            // Store flash error message
            SessionUtil.setErrorMessage(httpRequest,
                "Please log in to access this page.");

            // Redirect to login page
            httpResponse.sendRedirect(
            	    httpRequest.getContextPath() + "/login?redirect=" +
            	    java.net.URLEncoder.encode(requestedUrl, "UTF-8")
            	);
            return;
        }

        // ---------------------------------------------------------------
        // 3. Check if the account is active (Null-Safe Protection)
        // ---------------------------------------------------------------
        var loggedInUser = SessionUtil.getLoggedInUser(httpRequest);
        if (loggedInUser != null) {
            String accountStatus = loggedInUser.getAccountStatus();
            if (!"ACTIVE".equals(accountStatus)) {
                SessionUtil.invalidateSession(httpRequest);
                SessionUtil.setErrorMessage(httpRequest,
                    "Your account has been suspended. Please contact the administrator.");
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
                return;
            }
        } else {
            // Fallback: If session flag states logged in but user context is missing, force re-auth
            SessionUtil.invalidateSession(httpRequest);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Session is completely valid and verified - pass the request through
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("[AuthFilter] Destroyed.");
    }
}