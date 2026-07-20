package com.realestate.util;

import java.util.regex.Pattern;

/**
 * ValidationUtil - Input Validation and Sanitization Utility
 *
 * Provides reusable validation methods used by servlets before
 * data is passed to the DAO layer. Also provides XSS sanitization
 * to strip dangerous HTML characters from user input.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class ValidationUtil {

    // ---------------------------------------------------------------
    // Compiled regex patterns (compiled once for performance)
    // ---------------------------------------------------------------
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^(\\+234|0)[789][01]\\d{8}$");

    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$");

    private static final Pattern MATRIC_PATTERN =
        Pattern.compile("^[A-Z0-9/]{5,20}$");

    /**
     * Private constructor - utility class, not instantiable.
     */
    private ValidationUtil() {}

    // ---------------------------------------------------------------
    // Null and Empty Checks
    // ---------------------------------------------------------------

    /**
     * Returns true if the string is null or contains only whitespace.
     *
     * @param value  The string to test
     * @return       true if null or blank
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Trims a string or returns empty string if null.
     *
     * @param value  The string to sanitize
     * @return       Trimmed string, never null
     */
    public static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // ---------------------------------------------------------------
    // Format Validators
    // ---------------------------------------------------------------

    /**
     * Validates an email address format.
     *
     * @param  email  The email string to validate
     * @return        true if the email format is valid
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a Nigerian phone number.
     * Accepts formats: 08012345678, +2348012345678
     *
     * @param  phone  The phone number string to validate
     * @return        true if the phone format is valid
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates a password against strength requirements:
     *   - Minimum 8 characters
     *   - At least one uppercase letter
     *   - At least one lowercase letter
     *   - At least one digit
     *   - At least one special character
     *
     * @param  password  The plain-text password to validate
     * @return           true if the password meets requirements
     */
    public static boolean isValidPassword(String password) {
        if (isNullOrEmpty(password)) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates a matriculation number format.
     *
     * @param  matric  The matric number to validate
     * @return         true if format is acceptable
     */
    public static boolean isValidMatricNumber(String matric) {
        if (isNullOrEmpty(matric)) return false;
        return MATRIC_PATTERN.matcher(matric.trim().toUpperCase()).matches();
    }

    /**
     * Validates that a price value is a positive number.
     *
     * @param  price  The price string from form input
     * @return        true if parseable as a positive double
     */
    public static boolean isValidPrice(String price) {
        if (isNullOrEmpty(price)) return false;
        try {
            double val = Double.parseDouble(price.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates a non-negative integer (for bedrooms, bathrooms, etc.)
     *
     * @param  value  The integer string from form input
     * @return        true if parseable as a non-negative integer
     */
    public static boolean isValidPositiveInt(String value) {
        if (isNullOrEmpty(value)) return false;
        try {
            return Integer.parseInt(value.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates that a string does not exceed a maximum length.
     *
     * @param  value      The string to check
     * @param  maxLength  The maximum allowed character count
     * @return            true if within limit
     */
    public static boolean isWithinLength(String value, int maxLength) {
        if (isNullOrEmpty(value)) return true;
        return value.trim().length() <= maxLength;
    }

    // ---------------------------------------------------------------
    // XSS Sanitization
    // ---------------------------------------------------------------

    /**
     * Sanitizes a string by escaping HTML special characters.
     * Prevents Cross-Site Scripting (XSS) attacks when user input
     * is rendered in JSP pages outside of JSTL's c:out tag.
     *
     * Escapes: & < > " '
     *
     * @param  input  The raw user-supplied string
     * @return        The HTML-escaped string
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input
            .replace("&",  "&amp;")
            .replace("<",  "&lt;")
            .replace(">",  "&gt;")
            .replace("\"", "&quot;")
            .replace("'",  "&#x27;");
    }

    /**
     * Validates a campus distance category value against
     * the allowed enumeration defined in the database schema.
     *
     * @param  distance  The campus distance string to validate
     * @return           true if it matches a valid category
     */
    public static boolean isValidCampusDistance(String distance) {
        if (isNullOrEmpty(distance)) return false;
        switch (distance.trim()) {
            case "ON_CAMPUS":
            case "LESS_5MIN":
            case "5_TO_10MIN":
            case "10_TO_15MIN":
            case "ABOVE_15MIN":
                return true;
            default:
                return false;
        }
    }

    /**
     * Validates a property status value.
     *
     * @param  status  The property status string to validate
     * @return         true if valid
     */
    public static boolean isValidPropertyStatus(String status) {
        if (isNullOrEmpty(status)) return false;
        switch (status.trim()) {
            case "PENDING":
            case "AVAILABLE":
            case "RESERVED":
            case "OCCUPIED":
            case "SUSPENDED":
                return true;
            default:
                return false;
        }
    }
}