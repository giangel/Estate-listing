package com.realestate.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CsrfGuard - CSRF Token Generation and Validation Utility
 *
 * Implements the Synchronizer Token Pattern:
 *   1. A random token is generated when the user session is created.
 *   2. The token is embedded as a hidden field in every HTML form.
 *   3. On form submission (POST), the submitted token is compared
 *      to the session token.
 *   4. Mismatch = request rejected.
 *
 * Token lifetime: same as the user session (30 minutes idle timeout).
 *
 * Usage in Servlet (POST handler):
 *   if (!CsrfGuard.isValidToken(request)) {
 *       response.sendError(403, "CSRF validation failed.");
 *       return;
 *   }
 *
 * Usage in JSP (inside every <form>):
 *   <input type="hidden" name="_csrf"
 *          value="${sessionScope.csrfToken}">
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class CsrfGuard {

    /** Session attribute key where the CSRF token is stored */
    public static final String CSRF_SESSION_KEY = "csrfToken";

    /** Form field name expected in POST requests */
    public static final String CSRF_FORM_FIELD  = "_csrf";

    /** Token byte length - 24 bytes = 32 base64 characters */
    private static final int TOKEN_BYTE_LENGTH = 24;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CsrfGuard() {}

    // ---------------------------------------------------------------
    // Token Generation
    // ---------------------------------------------------------------

    /**
     * Generates a cryptographically secure random CSRF token.
     * Uses URL-safe Base64 encoding to ensure the token is safe
     * for embedding in HTML forms and URLs without encoding issues.
     *
     * @return  A 32-character URL-safe Base64 token string
     */
    public static String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // ---------------------------------------------------------------
    // Token Storage
    // ---------------------------------------------------------------

    /**
     * Retrieves the CSRF token from the current session.
     * Creates and stores a new token if one does not yet exist.
     *
     * @param  request  The current HTTP request
     * @return          The session's CSRF token string
     */
    public static String getOrCreateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (token == null || token.isEmpty()) {
            token = generateToken();
            session.setAttribute(CSRF_SESSION_KEY, token);
        }
        return token;
    }

    // ---------------------------------------------------------------
    // Token Validation
    // ---------------------------------------------------------------

    /**
     * Validates the CSRF token submitted with a POST form against
     * the token stored in the user's session.
     *
     * Validation fails if:
     *   - No session exists
     *   - No session token is stored
     *   - No form token was submitted
     *   - The tokens do not match (constant-time comparison)
     *
     * @param  request  The current HTTP POST request
     * @return          true if the tokens match, false otherwise
     */
    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_KEY);
        String formToken    = request.getParameter(CSRF_FORM_FIELD);

        if (sessionToken == null || formToken == null) return false;
        if (sessionToken.isEmpty() || formToken.isEmpty()) return false;

        // Constant-time comparison to prevent timing attacks
        return constantTimeEquals(sessionToken, formToken);
    }

    /**
     * Constant-time string comparison.
     * Prevents timing side-channel attacks where comparing character by
     * character and returning early on mismatch leaks information about
     * how many characters were correct.
     *
     * @param  a  First string
     * @param  b  Second string
     * @return    true if strings are identical
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    // ---------------------------------------------------------------
    // Helper for Servlets
    // ---------------------------------------------------------------

    /**
     * Rejects a request that fails CSRF validation.
     * Sends HTTP 403 Forbidden with a descriptive message.
     * Logs the event to the audit log.
     *
     * Usage:
     *   if (!CsrfGuard.isValidToken(request)) {
     *       CsrfGuard.rejectRequest(request, response);
     *       return;
     *   }
     *
     * @param  request   The current HTTP request
     * @param  response  The current HTTP response
     * @throws java.io.IOException if sendError fails
     */
    public static void rejectRequest(HttpServletRequest request,
                                     jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {

        System.err.println("[CsrfGuard] CSRF validation FAILED" +
                           " | URI: "  + request.getRequestURI() +
                           " | IP: "   + request.getRemoteAddr() +
                           " | User: " + SessionUtil.getLoggedInUserId(request));

        response.sendError(
            jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN,
            "Request validation failed. Please go back and try again."
        );
    }
}