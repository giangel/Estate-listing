package com.realestate.util;

import java.util.regex.Pattern;

/**
 * InputSanitizer - Multi-Context Input Sanitization Utility
 *
 * Provides sanitization methods for different output contexts:
 *
 *   sanitizeHtml()       - For text rendered in HTML (escapes HTML entities)
 *   sanitizeForJs()      - For text injected into JavaScript strings
 *   sanitizeFileName()   - For user-supplied file names
 *   stripScriptTags()    - Removes <script> tags from rich-text input
 *   sanitizeEmail()      - Normalizes email addresses
 *   sanitizePhone()      - Normalizes Nigerian phone numbers
 *
 * IMPORTANT: These methods complement, not replace, PreparedStatements.
 * SQL injection is prevented at the DAO layer via PreparedStatement.
 * These sanitizers protect against XSS in the presentation layer.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class InputSanitizer {

    // ---------------------------------------------------------------
    // Compiled Patterns (thread-safe, compiled once)
    // ---------------------------------------------------------------

    /** Matches <script> and </script> tags (case-insensitive) */
    private static final Pattern SCRIPT_TAG_PATTERN =
        Pattern.compile("<script[^>]*>.*?</script>",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** Matches javascript: URI schemes */
    private static final Pattern JAVASCRIPT_URI_PATTERN =
        Pattern.compile("javascript\\s*:",
                        Pattern.CASE_INSENSITIVE);

    /** Matches event handler attributes (onclick, onload, etc.) */
    private static final Pattern EVENT_HANDLER_PATTERN =
        Pattern.compile("on\\w+\\s*=",
                        Pattern.CASE_INSENSITIVE);

    /** Matches characters illegal in file names */
    private static final Pattern ILLEGAL_FILENAME_CHARS =
        Pattern.compile("[^a-zA-Z0-9._\\-]");

    /** Matches path traversal sequences */
    private static final Pattern PATH_TRAVERSAL_PATTERN =
        Pattern.compile("\\.\\./|\\.\\.\\\\ ");

    private InputSanitizer() {}

    // ---------------------------------------------------------------
    // HTML Sanitization
    // ---------------------------------------------------------------

    /**
     * Escapes HTML special characters to prevent XSS.
     * This is the primary defense for rendering user input in JSP pages.
     *
     * Escapes: & < > " ' `
     *
     * When to use:
     *   - Any text rendered between HTML tags
     *   - Any text rendered in HTML attribute values
     *
     * Note: JSTL's <c:out> performs the same escaping automatically.
     * Use this method in servlets or EL expressions where <c:out> is
     * not available.
     *
     * @param  input  Raw user-supplied string
     * @return        HTML-escaped string (never null)
     */
    public static String sanitizeHtml(String input) {
        if (input == null) return "";
        return input
            .replace("&",  "&amp;")
            .replace("<",  "&lt;")
            .replace(">",  "&gt;")
            .replace("\"", "&quot;")
            .replace("'",  "&#x27;")
            .replace("`",  "&#x60;")
            .replace("/",  "&#x2F;");
    }

    /**
     * Escapes a string for safe injection into a JavaScript string literal.
     *
     * When to use:
     *   - Setting JavaScript variables from server-side data
     *   - Injecting data into onclick attributes (prefer data attributes instead)
     *
     * Example (JSP):
     *   var title = "<%= InputSanitizer.sanitizeForJs(property.getTitle()) %>";
     *
     * @param  input  Raw string to inject into JavaScript
     * @return        JavaScript-safe string
     */
    public static String sanitizeForJs(String input) {
        if (input == null) return "";
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("'",  "\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("</", "<\\/")
            .replace("<script", "<scr\"+\"ipt");
    }

    /**
     * Removes script tags and javascript: URI schemes from input.
     * Suitable for sanitizing rich-text or HTML-like input before storage.
     *
     * Note: For robust HTML sanitization in a production system,
     * consider a dedicated library like OWASP Java HTML Sanitizer.
     * This method handles common attack vectors.
     *
     * @param  input  Raw HTML or rich-text string
     * @return        String with dangerous HTML removed
     */
    public static String stripDangerousHtml(String input) {
        if (input == null) return "";
        String result = SCRIPT_TAG_PATTERN.matcher(input).replaceAll("");
        result = JAVASCRIPT_URI_PATTERN.matcher(result).replaceAll("#");
        result = EVENT_HANDLER_PATTERN.matcher(result).replaceAll("data-blocked=");
        return result.trim();
    }

    // ---------------------------------------------------------------
    // File Name Sanitization
    // ---------------------------------------------------------------

    /**
     * Sanitizes a file name to prevent path traversal and
     * illegal character attacks.
     *
     * Steps:
     *   1. Extract only the file name component (strip any path)
     *   2. Replace characters outside [a-zA-Z0-9._-] with underscore
     *   3. Remove path traversal sequences (../)
     *   4. Enforce maximum length of 100 characters
     *
     * @param  fileName  The raw file name from the upload
     * @return           A sanitized, safe file name
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "upload_" + System.currentTimeMillis();
        }

        // Extract only the base name (strip any path component)
        String baseName = fileName
            .replace("\\", "/")
            .substring(fileName.replace("\\", "/").lastIndexOf("/") + 1);

        // Remove path traversal sequences
        baseName = PATH_TRAVERSAL_PATTERN.matcher(baseName).replaceAll("");

        // Replace illegal characters
        baseName = ILLEGAL_FILENAME_CHARS.matcher(baseName).replaceAll("_");

        // Enforce length limit
        if (baseName.length() > 100) {
            int dotIndex = baseName.lastIndexOf('.');
            if (dotIndex > 0) {
                String ext  = baseName.substring(dotIndex);
                baseName    = baseName.substring(0, 96) + ext;
            } else {
                baseName = baseName.substring(0, 100);
            }
        }

        // If the result is empty after cleaning, use a timestamp name
        return baseName.isEmpty()
               ? "upload_" + System.currentTimeMillis()
               : baseName;
    }

    // ---------------------------------------------------------------
    // Contact Field Sanitization
    // ---------------------------------------------------------------

    /**
     * Normalizes an email address.
     * Trims whitespace and converts to lowercase.
     *
     * @param  email  Raw email input
     * @return        Normalized email (never null)
     */
    public static String sanitizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }

    /**
     * Normalizes a Nigerian phone number to the standard 11-digit format.
     * Removes spaces, dashes, and parentheses.
     * Converts +234XXXXXXXX to 0XXXXXXXX.
     *
     * @param  phone  Raw phone number input
     * @return        Normalized phone number string
     */
    public static String sanitizePhone(String phone) {
        if (phone == null) return "";

        // Remove all non-digit and non-plus characters
        String cleaned = phone.replaceAll("[^\\d+]", "");

        // Convert +234XXXXXXXXXX to 0XXXXXXXXXX
        if (cleaned.startsWith("+234")) {
            cleaned = "0" + cleaned.substring(4);
        }

        return cleaned;
    }

    // ---------------------------------------------------------------
    // Text Length Enforcement
    // ---------------------------------------------------------------

    /**
     * Truncates a string to a maximum length, appending "..." if truncated.
     * Useful for display fields to prevent UI overflow from abnormally
     * long user input.
     *
     * @param  input      The string to truncate
     * @param  maxLength  Maximum character count
     * @return            Truncated string
     */
    public static String truncate(String input, int maxLength) {
        if (input == null) return "";
        if (input.length() <= maxLength) return input;
        return input.substring(0, maxLength - 3) + "...";
    }

    /**
     * Returns true if the input contains any HTML tags.
     * Used to flag inputs that should be plain text but contain markup.
     *
     * @param  input  The string to test
     * @return        true if the string contains HTML angle brackets
     */
    public static boolean containsHtml(String input) {
        if (input == null) return false;
        return input.contains("<") && input.contains(">");
    }
}