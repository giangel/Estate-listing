package com.realestate.util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/**
 * SecureUploadValidator - Deep File Upload Security Validation
 *
 * Provides multi-layer file upload validation:
 *
 *   Layer 1: File extension whitelist check
 *   Layer 2: MIME type whitelist check (from Content-Type header)
 *   Layer 3: Magic byte signature verification
 *             (reads actual file header bytes, not browser-declared type)
 *   Layer 4: File size limit enforcement
 *
 * Why magic bytes?
 *   An attacker can rename a PHP script to "photo.jpg" and upload it.
 *   The browser sends Content-Type: image/jpeg, which passes basic checks.
 *   By reading the first few bytes of the file, we verify the actual
 *   file format matches the declared type.
 *
 * Supported formats:
 *   JPEG: FF D8 FF
 *   PNG:  89 50 4E 47 0D 0A 1A 0A
 *   WebP: 52 49 46 46 ?? ?? ?? ?? 57 45 42 50
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class SecureUploadValidator {

    /** Maximum file size: 5 MB */
    public static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L;

    /** JPEG magic bytes */
    private static final byte[] JPEG_MAGIC = {(byte)0xFF, (byte)0xD8, (byte)0xFF};

    /** PNG magic bytes */
    private static final byte[] PNG_MAGIC  = {
        (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    /** WebP magic bytes - RIFF at byte 0 and WEBP at byte 8 */
    private static final byte[] WEBP_RIFF  = {0x52, 0x49, 0x46, 0x46};
    private static final byte[] WEBP_WEBP  = {0x57, 0x45, 0x42, 0x50};

    /** Number of bytes to read for magic byte check */
    private static final int HEADER_BYTES  = 12;

    private SecureUploadValidator() {}

    // ---------------------------------------------------------------
    // Public Validation Interface
    // ---------------------------------------------------------------

    /**
     * Validation result containing the outcome and a descriptive message.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String  message;

        private ValidationResult(boolean valid, String message) {
            this.valid   = valid;
            this.message = message;
        }

        public boolean isValid()   { return valid; }
        public String getMessage() { return message; }

        static ValidationResult ok() {
            return new ValidationResult(true, "File is valid.");
        }

        static ValidationResult fail(String msg) {
            return new ValidationResult(false, msg);
        }
    }

    /**
     * Performs all four validation layers on an uploaded Part.
     *
     * @param  part  The uploaded file Part
     * @return       ValidationResult with outcome and message
     */
    public static ValidationResult validate(Part part) {
        if (part == null || part.getSize() == 0) {
            return ValidationResult.fail("No file was uploaded.");
        }

        // ---------------------------------------------------------------
        // Layer 1: File size
        // ---------------------------------------------------------------
        if (part.getSize() > MAX_FILE_SIZE_BYTES) {
            return ValidationResult.fail(
                "File size exceeds the 5 MB limit. " +
                "Uploaded size: " + (part.getSize() / 1048576) + " MB.");
        }

        // ---------------------------------------------------------------
        // Layer 2: File extension
        // ---------------------------------------------------------------
        String fileName = getSubmittedFileName(part);
        if (!hasValidExtension(fileName)) {
            return ValidationResult.fail(
                "Invalid file type: " + getExtension(fileName) + ". " +
                "Only JPEG, PNG, and WebP images are accepted.");
        }

        // ---------------------------------------------------------------
        // Layer 3: MIME type (browser-declared)
        // ---------------------------------------------------------------
        String contentType = part.getContentType();
        if (!hasValidMimeType(contentType)) {
            return ValidationResult.fail(
                "Rejected MIME type: " + contentType + ". " +
                "Only image/jpeg, image/png, and image/webp are accepted.");
        }

        // ---------------------------------------------------------------
        // Layer 4: Magic byte signature (actual file content)
        // ---------------------------------------------------------------
        try {
            byte[] header = readHeader(part);
            if (!hasValidMagicBytes(header)) {
                return ValidationResult.fail(
                    "File content does not match its extension. " +
                    "The uploaded file does not appear to be a valid image.");
            }
        } catch (IOException e) {
            return ValidationResult.fail(
                "Could not read file content for validation.");
        }

        return ValidationResult.ok();
    }

    // ---------------------------------------------------------------
    // Private Validation Helpers
    // ---------------------------------------------------------------

    /**
     * Returns true if the file extension is in the allowed set.
     */
    private static boolean hasValidExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return false;
        String ext = getExtension(fileName).toLowerCase();
        return ext.equals(".jpg")  ||
               ext.equals(".jpeg") ||
               ext.equals(".png")  ||
               ext.equals(".webp");
    }

    /**
     * Returns true if the MIME type is in the allowed set.
     */
    private static boolean hasValidMimeType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) return false;
        String type = mimeType.toLowerCase().trim();
        return type.equals("image/jpeg") ||
               type.equals("image/jpg")  ||
               type.equals("image/png")  ||
               type.equals("image/webp");
    }

    /**
     * Reads the first HEADER_BYTES bytes from the uploaded file.
     * This is the file "magic" - the actual binary signature.
     *
     * @param  part  The uploaded Part
     * @return       Byte array of the file header
     * @throws IOException if the stream cannot be read
     */
    private static byte[] readHeader(Part part) throws IOException {
        byte[] header = new byte[HEADER_BYTES];
        try (InputStream is = part.getInputStream()) {
            int bytesRead = is.read(header);
            if (bytesRead < 3) {
                return new byte[0]; // Not enough bytes to identify
            }
        }
        return header;
    }

    /**
     * Verifies that the file header matches a known image format.
     *
     * @param  header  The file header bytes
     * @return         true if the bytes match JPEG, PNG, or WebP signatures
     */
    private static boolean hasValidMagicBytes(byte[] header) {
        if (header == null || header.length < 3) return false;
        return isJpeg(header) || isPng(header) || isWebP(header);
    }

    /**
     * Checks JPEG magic bytes: FF D8 FF
     */
    private static boolean isJpeg(byte[] h) {
        return h.length >= 3 &&
               h[0] == JPEG_MAGIC[0] &&
               h[1] == JPEG_MAGIC[1] &&
               h[2] == JPEG_MAGIC[2];
    }

    /**
     * Checks PNG magic bytes: 89 50 4E 47 0D 0A 1A 0A
     */
    private static boolean isPng(byte[] h) {
        if (h.length < 8) return false;
        for (int i = 0; i < PNG_MAGIC.length; i++) {
            if (h[i] != PNG_MAGIC[i]) return false;
        }
        return true;
    }

    /**
     * Checks WebP magic bytes:
     *   Bytes 0–3: RIFF (52 49 46 46)
     *   Bytes 8–11: WEBP (57 45 42 50)
     */
    private static boolean isWebP(byte[] h) {
        if (h.length < 12) return false;
        for (int i = 0; i < WEBP_RIFF.length; i++) {
            if (h[i] != WEBP_RIFF[i]) return false;
        }
        for (int i = 0; i < WEBP_WEBP.length; i++) {
            if (h[i + 8] != WEBP_WEBP[i]) return false;
        }
        return true;
    }

    // ---------------------------------------------------------------
    // File Name Helpers
    // ---------------------------------------------------------------

    /**
     * Extracts the submitted file name from the Content-Disposition header.
     */
    public static String getSubmittedFileName(Part part) {
        if (part == null) return "";
        String header = part.getHeader("content-disposition");
        if (header == null) return "";
        for (String token : header.split(";")) {
            String trimmed = token.trim();
            if (trimmed.startsWith("filename")) {
                return trimmed.substring(trimmed.indexOf('=') + 1)
                              .trim()
                              .replace("\"", "");
            }
        }
        return "";
    }

    /**
     * Returns the file extension including the dot.
     * Example: "photo.jpg" → ".jpg"
     */
    private static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}