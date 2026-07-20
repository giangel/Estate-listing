package com.realestate.util;

import com.realestate.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * SessionUtil - HTTP Session Management Utility
 *
 * Provides a clean, centralized API for all session operations.
 * Prevents session attribute name typos by using constants and
 * standardizes login/logout session management across servlets.
 *
 * Session Attributes Stored on Login:
 * - SESSION_USER      : User object (full user record)
 * - SESSION_USER_ID   : int (user's primary key)
 * - SESSION_USER_NAME : String (user's full name)
 * - SESSION_USER_EMAIL: String (user's email)
 * - SESSION_ROLE      : String (role name: ADMIN, STUDENT, etc.)
 * - SESSION_ROLE_ID   : int (role primary key)
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class SessionUtil {

    // ---------------------------------------------------------------
    // Session attribute name constants
    // ---------------------------------------------------------------
    public static final String SESSION_USER       = "loggedInUser";
    public static final String SESSION_USER_ID    = "userId";
    public static final String SESSION_USER_NAME  = "userName";
    public static final String SESSION_USER_EMAIL = "userEmail";
    public static final String SESSION_ROLE       = "userRole";
    public static final String SESSION_ROLE_ID    = "userRoleId";
    public static final String SESSION_CSRF_TOKEN = "csrfToken";

    /**
     * Private constructor - utility class, not instantiable.
     */
    private SessionUtil() {}

    // ---------------------------------------------------------------
    // Session Creation
    // ---------------------------------------------------------------

    /**
     * Creates a new authenticated session for the given user.
     * Invalidates any existing session first to prevent session fixation.
     *
     * @param request  The current HTTP request
     * @param user     The authenticated User object
     */
    public static void createSession(HttpServletRequest request, User user) {
        // Invalidate old session to prevent session fixation attacks
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Create a new session
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(30 * 60); // 30 minutes

        // Store user attributes
        session.setAttribute(SESSION_USER,       user);
        session.setAttribute(SESSION_USER_ID,    user.getUserId());
        session.setAttribute(SESSION_USER_NAME,  user.getFullName());
        session.setAttribute(SESSION_USER_EMAIL, user.getEmail());
        session.setAttribute(SESSION_ROLE,       user.getRoleName());
        session.setAttribute(SESSION_ROLE_ID,    user.getRoleId());

        // Generate and store CSRF token
        session.setAttribute(SESSION_CSRF_TOKEN, PasswordUtil.generateResetToken());
    }

    // ---------------------------------------------------------------
    // Session Reading
    // ---------------------------------------------------------------

    /**
     * Returns the logged-in User object from the current session.
     *
     * @param  request  The current HTTP request
     * @return          The User object, or null if not authenticated
     */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute(SESSION_USER);
    }

    /**
     * Returns the logged-in user's ID from the session.
     *
     * @param  request  The current HTTP request
     * @return          The user ID, or -1 if not authenticated
     */
    public static int getLoggedInUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return -1;
        Object id = session.getAttribute(SESSION_USER_ID);
        return (id instanceof Integer) ? (Integer) id : -1;
    }

    /**
     * Returns the logged-in user's role name from the session.
     *
     * @param  request  The current HTTP request
     * @return          Role name string (e.g., "ADMIN"), or null
     */
    public static String getLoggedInRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute(SESSION_ROLE);
    }

    // ---------------------------------------------------------------
    // Authentication Checks
    // ---------------------------------------------------------------

    /**
     * Returns true if a valid authenticated session exists.
     *
     * @param  request  The current HTTP request
     * @return          true if user is logged in
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInUser(request) != null;
    }

    /**
     * Returns true if the logged-in user has the ADMIN role.
     *
     * @param  request  The current HTTP request
     * @return          true if user is an administrator
     */
    public static boolean isAdmin(HttpServletRequest request) {
        return "ADMIN".equals(getLoggedInRole(request));
    }

    /**
     * Returns true if the logged-in user has the LANDLORD role.
     *
     * @param  request  The current HTTP request
     * @return          true if user is a landlord
     */
    public static boolean isLandlord(HttpServletRequest request) {
        return "LANDLORD".equals(getLoggedInRole(request));
    }

    /**
     * Returns true if the logged-in user has the AGENT role.
     *
     * @param  request  The current HTTP request
     * @return          true if user is a verified agent
     */
    public static boolean isAgent(HttpServletRequest request) {
        return "AGENT".equals(getLoggedInRole(request));
    }

    /**
     * Returns true if the logged-in user has the STUDENT role.
     *
     * @param  request  The current HTTP request
     * @return          true if user is a student
     */
    public static boolean isStudent(HttpServletRequest request) {
        return "STUDENT".equals(getLoggedInRole(request));
    }

    /**
     * Returns true if the user holds a property-listing role
     * (LANDLORD or AGENT).
     *
     * @param  request  The current HTTP request
     * @return          true if user can create listings
     */
    public static boolean canCreateListing(HttpServletRequest request) {
        String role = getLoggedInRole(request);
        return "LANDLORD".equals(role) || "AGENT".equals(role) || "ADMIN".equals(role);
    }

    // ---------------------------------------------------------------
    // Flash Messages (one-time display messages after redirect)
    // ---------------------------------------------------------------

    /**
     * Stores a success flash message to be displayed after redirect.
     *
     * @param request  The current HTTP request
     * @param message  The success message text
     */
    public static void setSuccessMessage(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute("successMessage", message);
    }

    /**
     * Stores an error flash message to be displayed after redirect.
     *
     * @param request  The current HTTP request
     * @param message  The error message text
     */
    public static void setErrorMessage(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute("errorMessage", message);
    }

    /**
     * Stores an informational alert flash message to be displayed after redirect.
     *
     * @param request  The current HTTP request
     * @param message  The informational alert message text
     */
    public static void setInfoMessage(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute("infoMessage", message);
    }

    /**
     * Retrieves and removes the success flash message from the session.
     *
     * @param  request  The current HTTP request
     * @return          The success message, or null if none set
     */
    public static String getAndClearSuccessMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        String msg = (String) session.getAttribute("successMessage");
        session.removeAttribute("successMessage");
        return msg;
    }

    /**
     * Retrieves and removes the error flash message from the session.
     *
     * @param  request  The current HTTP request
     * @return          The error message, or null if none set
     */
    public static String getAndClearErrorMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        String msg = (String) session.getAttribute("errorMessage");
        session.removeAttribute("errorMessage");
        return msg;
    }

    /**
     * Retrieves and removes the info flash message from the session.
     *
     * @param  request  The current HTTP request
     * @return          The info message, or null if none set
     */
    public static String getAndClearInfoMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        String msg = (String) session.getAttribute("infoMessage");
        session.removeAttribute("infoMessage");
        return msg;
    }

    // ---------------------------------------------------------------
    // CSRF Token Validation
    // ---------------------------------------------------------------

    /**
     * Validates a CSRF token submitted with a form against the
     * token stored in the user's session.
     *
     * @param  request    The current HTTP request
     * @param  formToken  The CSRF token value from the form hidden field
     * @return            true if the tokens match
     */
    public static boolean isValidCsrfToken(HttpServletRequest request, String formToken) {
        HttpSession session = request.getSession(false);
        if (session == null || formToken == null) return false;
        String sessionToken = (String) session.getAttribute(SESSION_CSRF_TOKEN);
        return formToken.equals(sessionToken);
    }

    // ---------------------------------------------------------------
    // Session Destruction
    // ---------------------------------------------------------------

    /**
     * Invalidate the current session completely.
     * Called on logout to clear all user data from memory.
     *
     * @param request  The current HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}