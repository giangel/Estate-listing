package com.realestate.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SecurityFilter - Global HTTP Security Headers Filter
 *
 * Applied to ALL requests (url-pattern = "/*").
 * Adds security headers to every HTTP response:
 *
 * X-Frame-Options          - Prevents clickjacking (embedding in iframes)
 * X-Content-Type-Options   - Prevents MIME type sniffing
 * X-XSS-Protection         - Legacy XSS filter for older browsers
 * Referrer-Policy          - Controls referer header information leakage
 * Cache-Control            - Prevents caching of sensitive authenticated pages
 * Content-Security-Policy  - Controls resource loading origins
 *
 * Register in web.xml with url-pattern "/*" and place BEFORE
 * AuthFilter and AdminFilter in filter ordering.
 *
 * @author  AOPE CS Department
 * @version 1.0
 *
 */
@WebFilter(filterName = "SecurityFilter", urlPatterns = {"/*"})
public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        System.out.println("[SecurityFilter] Initialized - security headers active.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpReq  = (HttpServletRequest)  request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        // ---------------------------------------------------------------
        // 1. Clickjacking Prevention
        // ---------------------------------------------------------------
        httpResp.setHeader("X-Frame-Options", "SAMEORIGIN");

        // ---------------------------------------------------------------
        // 2. MIME Sniffing Prevention
        // ---------------------------------------------------------------
        httpResp.setHeader("X-Content-Type-Options", "nosniff");

        // ---------------------------------------------------------------
        // 3. Legacy XSS Filter
        // ---------------------------------------------------------------
        httpResp.setHeader("X-XSS-Protection", "1; mode=block");

        // ---------------------------------------------------------------
        // 4. Referrer Policy
        // ---------------------------------------------------------------
        httpResp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // ---------------------------------------------------------------
        // 5. Cache Control for Authenticated Pages
        // ---------------------------------------------------------------
        String requestURI = httpReq.getRequestURI();
        if (isProtectedRoute(requestURI)) {
            httpResp.setHeader("Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0");
            httpResp.setHeader("Pragma", "no-cache");
            httpResp.setHeader("Expires", "0");
        }

        // ---------------------------------------------------------------
        // 6. Content Security Policy (CSP)
        //    FIX: Added trusted layout domains to connect-src rule
        // ---------------------------------------------------------------
        httpResp.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' " +
                "cdn.jsdelivr.net " +
                "cdnjs.cloudflare.com; " +
            "style-src 'self' 'unsafe-inline' " +
                "cdn.jsdelivr.net " +
                "cdnjs.cloudflare.com " +
                "fonts.googleapis.com; " +
            "font-src 'self' data: " +
                "fonts.gstatic.com " +
                "cdnjs.cloudflare.com " +
                "cdn.jsdelivr.net; " +
            "img-src 'self' data: blob:; " +
            "connect-src 'self' " +
                "cdn.jsdelivr.net " +
                "cdnjs.cloudflare.com " +
                "fonts.googleapis.com " +
                "fonts.gstatic.com; " +
            "frame-ancestors 'self';"
        );

        // ---------------------------------------------------------------
        // 7. Remove Server Header (neutralizes information leakage)
        // ---------------------------------------------------------------
        httpResp.setHeader("Server", "AOP-RealEstate/1.0");

        // Pass request through the filter chain
        chain.doFilter(request, response);
    }

    private boolean isProtectedRoute(String uri) {
        return uri.contains("/admin/")  ||
               uri.contains("/user/")   ||
               uri.contains("/landlord/") ||
               uri.contains("/agent/")  ||
               uri.contains("/roommate/");
    }

    @Override
    public void destroy() {
        System.out.println("[SecurityFilter] Destroyed.");
    }
}