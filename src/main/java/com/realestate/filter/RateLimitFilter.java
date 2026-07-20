package com.realestate.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RateLimitFilter - Brute-Force Login Protection
 *
 * Tracks POST requests to /login per IP address.
 * Blocks IPs that exceed MAX_ATTEMPTS within WINDOW_MILLIS.
 *
 * Configuration:
 * MAX_ATTEMPTS   = 10 login attempts
 * WINDOW_MILLIS  = 15 minutes
 * BLOCK_DURATION = 30 minutes
 *
 * Storage: In-memory ConcurrentHashMap.
 * (For production with multiple Tomcat nodes, use Redis or database.)
 *
 * @author  AOPE CS Department
 * @version 1.2
 */
@WebFilter(filterName = "RateLimitFilter", urlPatterns = {"/login"})
public class RateLimitFilter implements Filter {

    /** Maximum login attempts allowed per IP within the time window */
    private static final int MAX_ATTEMPTS     = 10;

    /** Time window in milliseconds (15 minutes) */
    private static final long WINDOW_MILLIS   = 15 * 60 * 1000L;

    /** Block duration in milliseconds (30 minutes) */
    private static final long BLOCK_DURATION  = 30 * 60 * 1000L;

    /** HTTP Status code 429 Too Many Requests */
    private static final int STATUS_TOO_MANY_REQUESTS = 429;

    /** Inner class tracking attempt count and window start time for an IP. */
    private static class AttemptRecord {
        AtomicInteger count       = new AtomicInteger(0);
        long          windowStart = System.currentTimeMillis();
        long          blockedUntil = 0;
    }

    /** Thread-safe map of IP → attempt record */
    private final Map<String, AttemptRecord> attemptMap = new ConcurrentHashMap<>();

    /** Handle to the background thread to safely manage context reloads */
    private Thread cleanerThread;

    @Override
    public void init(FilterConfig config) throws ServletException {
        System.out.println("[RateLimitFilter] Login rate limiting active " +
                           "(" + MAX_ATTEMPTS + " attempts / 15 min).");

        // Background cleanup thread - removes stale records every 10 minutes
        cleanerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(10 * 60 * 1000L);
                    cleanStaleRecords();
                } catch (InterruptedException e) {
                    // Re-assert interrupt status to cleanly exit the loop
                    Thread.currentThread().interrupt();
                }
            }
        }, "RateLimit-Cleaner");
        
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpReq  = (HttpServletRequest)  request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        // Only rate-limit POST requests (login form submissions)
        if (!"POST".equalsIgnoreCase(httpReq.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String ipAddress = getClientIpAddress(httpReq);
        AttemptRecord record = attemptMap.computeIfAbsent(ipAddress, k -> new AttemptRecord());

        long now = System.currentTimeMillis();

        // Check if IP is currently blocked
        if (record.blockedUntil > now) {
            long remainingMinutes = (record.blockedUntil - now) / 60000;
            System.err.println("[RateLimitFilter] Blocked IP access attempt: " + ipAddress);
            
            httpResp.setStatus(STATUS_TOO_MANY_REQUESTS);
            httpResp.setContentType("text/html;charset=UTF-8");
            httpResp.getWriter().write(
                "<!DOCTYPE html><html><head><title>Too Many Requests</title></head>" +
                "<body style='font-family:sans-serif;text-align:center;padding:60px'>" +
                "<h2 style='color:#dc3545'>Too Many Login Attempts</h2>" +
                "<p>Your IP address has been temporarily blocked due to repeated failed login attempts.</p>" +
                "<p>Please try again in approximately <strong>" + remainingMinutes + " minutes</strong>.</p>" +
                "<p style='color:#6c757d;font-size:0.875rem'>If you believe this is an error, contact the administrator.</p>" +
                "</body></html>"
            );
            return;
        }

        // Reset counter if outside the current window
        if (now - record.windowStart > WINDOW_MILLIS) {
            record.count.set(0);
            record.windowStart = now;
        }

        // Increment attempt count
        int currentCount = record.count.incrementAndGet();

        if (currentCount > MAX_ATTEMPTS) {
            // Block the IP
            record.blockedUntil = now + BLOCK_DURATION;
            record.count.set(0);
            System.err.println("[RateLimitFilter] IP blocked for 30 min: " +
                               ipAddress + " (exceeded " + MAX_ATTEMPTS + " attempts)");
            
            httpResp.setStatus(STATUS_TOO_MANY_REQUESTS);
            httpResp.setContentType("text/html;charset=UTF-8");
            httpResp.getWriter().write(
                "Too many login attempts. Your IP has been blocked for 30 minutes."
            );
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Extracts the real client IP address, accounting for reverse proxies.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For may contain a chain; take the first
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Removes stale (expired) records from the attempt map.
     */
    private void cleanStaleRecords() {
        long now = System.currentTimeMillis();
        attemptMap.entrySet().removeIf(entry -> {
            AttemptRecord record = entry.getValue();
            return (now - record.windowStart > WINDOW_MILLIS * 2) && record.blockedUntil < now;
        });
    }

    @Override
    public void destroy() {
        // CRITICAL FIX: Explicitly signal background thread to exit loop and interrupt its sleep state
        if (cleanerThread != null) {
            cleanerThread.interrupt();
        }
        attemptMap.clear();
        System.out.println("[RateLimitFilter] Destroyed - Background cleaner thread stopped.");
    }
}