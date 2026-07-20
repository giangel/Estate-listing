package com.realestate.util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * FileUploadUtil - Secure File Upload Utility
 *
 * Handles multipart/form-data image uploads for property listings
 * and user profile photos. Enforces:
 *   - File type whitelist (JPEG, PNG, WebP only)
 *   - Maximum file size limit (5 MB)
 *   - Unique filename generation (UUID-based)
 *   - Storage outside the web root for security
 *
 * Upload Directory Structure:
 *   /uploads/properties/{propertyId}/{uuid}.jpg
 *   /uploads/profiles/{uuid}.jpg
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class FileUploadUtil {

    /** Maximum allowed file size: 5 MB */
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;

    /** Allowed MIME types for image uploads */
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    ));

    /** Allowed file extensions */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        ".jpg", ".jpeg", ".png", ".webp"
    ));

    /**
     * Private constructor - utility class, not instantiable.
     */
    private FileUploadUtil() {}

    /**
     * Validates that an uploaded Part is an acceptable image file.
     * Checks both MIME type and file extension.
     *
     * @param  part  The uploaded file Part from the servlet request
     * @return       true if the file is a valid, non-empty image
     */
    public static boolean isValidImage(Part part) {
        if (part == null || part.getSize() == 0) return false;
        if (part.getSize() > MAX_FILE_SIZE)       return false;

        // Check MIME type
        String contentType = part.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        // Check file extension
        String fileName = getSubmittedFileName(part);
        if (fileName == null || fileName.isEmpty()) return false;

        String ext = getFileExtension(fileName).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    /**
     * Saves an uploaded image to the server filesystem.
     *
     * The file is saved to: {uploadBaseDir}/properties/{propertyId}/{uuid}{ext}
     *
     * @param  part          The uploaded file Part
     * @param  uploadBaseDir The base upload directory path (absolute)
     * @param  propertyId    The ID of the property these images belong to
     * @return               The relative path stored in the database,
     *                       e.g., "uploads/properties/5/abc123.jpg"
     * @throws IOException   If file cannot be saved
     */
    public static String savePropertyImage(Part part, String uploadBaseDir,
                                           int propertyId) throws IOException {
        String originalName = getSubmittedFileName(part);
        String extension    = getFileExtension(originalName).toLowerCase();
        String uniqueName   = UUID.randomUUID().toString() + extension;

        // Build directory path
        String dirPath = uploadBaseDir + File.separator
                       + "properties" + File.separator + propertyId;
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file
        String fullPath = dirPath + File.separator + uniqueName;
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
        }

        // Return relative path for database storage
        return "uploads/properties/" + propertyId + "/" + uniqueName;
    }

    /**
     * Saves a user profile photo to the server filesystem.
     *
     * @param  part          The uploaded file Part
     * @param  uploadBaseDir The base upload directory path (absolute)
     * @param  userId        The ID of the user
     * @return               The relative path stored in the database,
     *                       e.g., "uploads/profiles/abc123.jpg"
     * @throws IOException   If file cannot be saved
     */
    public static String saveProfilePhoto(Part part, String uploadBaseDir,
                                          int userId) throws IOException {
        String originalName = getSubmittedFileName(part);
        String extension    = getFileExtension(originalName).toLowerCase();
        String uniqueName   = "user_" + userId + "_" + UUID.randomUUID().toString() + extension;

        String dirPath = uploadBaseDir + File.separator + "profiles";
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fullPath = dirPath + File.separator + uniqueName;
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
        }

        return "uploads/profiles/" + uniqueName;
    }

    /**
     * Deletes a file from the server filesystem.
     * Used when a listing is deleted or an image is replaced.
     *
     * @param  uploadBaseDir  The base upload directory
     * @param  relativePath   The relative path stored in the database
     * @return                true if the file was deleted successfully
     */
    public static boolean deleteFile(String uploadBaseDir, String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return false;
        File file = new File(uploadBaseDir + File.separator
                           + relativePath.replace("/", File.separator));
        return file.exists() && file.delete();
    }

    /**
     * Extracts the submitted file name from the Content-Disposition header.
     * Compatible with all browsers.
     *
     * @param  part  The uploaded file Part
     * @return       The original filename, or empty string if not found
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
     * Extracts the file extension from a filename.
     *
     * @param  fileName  The original filename (e.g., "photo.jpg")
     * @return           The extension with dot (e.g., ".jpg"),
     *                   or empty string if no extension found
     */
    private static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}