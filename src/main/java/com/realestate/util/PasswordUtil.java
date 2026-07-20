package com.realestate.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil - Password Hashing and Verification Utility
 *
 * Uses BCrypt to securely hash and verify passwords.
 * BCrypt automatically generates and embeds a salt in
 * each hash, making rainbow table attacks infeasible.
 *
 * Work factor: 12 (increases hashing time to deter brute-force)
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class PasswordUtil {

    /**
     * BCrypt work factor (cost factor).
     * Higher value = stronger security, but slower hashing.
     * 12 is appropriate for production use.
     */
    private static final int WORK_FACTOR = 12;

    /**
     * Private constructor - utility class, not instantiable.
     */
    private PasswordUtil() {}

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * Example:
     *   String hash = PasswordUtil.hashPassword("MyPassword@1");
     *   // Result: "$2a$12$..." (60-character BCrypt hash)
     *
     * @param  plainTextPassword  The user's plain-text password
     * @return                    The BCrypt hash string
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * Example:
     *   boolean valid = PasswordUtil.verifyPassword("MyPassword@1", storedHash);
     *
     * @param  plainTextPassword  The plain-text password to check
     * @param  hashedPassword     The stored BCrypt hash from the database
     * @return                    true if password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            // Malformed hash in database - log and return false
            System.err.println("[PasswordUtil] Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a cryptographically secure random token.
     * Used for password reset links.
     *
     * @return A 32-character hexadecimal token string
     */
    public static String generateResetToken() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}