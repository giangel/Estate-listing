package com.realestate.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Database Connection Utility
 *
 * Provides a centralized, reusable PostgreSQL connection for the
 * Real Estate Listing System. Uses the Singleton pattern to ensure
 * a single point of database configuration.
 *
 * Configuration:
 *   Database : realestate_aop (or from env)
 *   Host     : localhost (or from env)
 *   Port     : 5432 (or from env)
 *   Username : postgres (or from env)
 *   Password : (set below or from env)
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class DBConnection {

    // ---------------------------------------------------------------
    // Database configuration constants
    // Uses environment variables if set, otherwise defaults to local
    // ---------------------------------------------------------------
    private static final String DB_URL = 
        System.getenv("DB_URL") != null ? System.getenv("DB_URL") 
        : "jdbc:postgresql://localhost:5432/realestate_aop";

    private static final String DB_USER = 
        System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";

    private static final String DB_PASSWORD = 
        System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "3693";
        
    private static final String DB_DRIVER = "org.postgresql.Driver";

    /**
     * Private constructor - prevents direct instantiation.
     */
    private DBConnection() {}

    /**
     * Loads the PostgreSQL JDBC driver.
     * Called once when the class is first used.
     */
    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] CRITICAL: PostgreSQL JDBC Driver not found.");
            System.err.println("[DBConnection] Ensure postgresql-42.x.x.jar is in WEB-INF/lib/");
            e.printStackTrace();
        }
    }

    /**
     * Returns a new database connection.
     *
     * Usage in DAO:
     *   Connection conn = DBConnection.getConnection();
     *
     * Always close the connection in a finally block or
     * use try-with-resources to prevent connection leaks.
     *
     * @return  A live {@link Connection} to the PostgreSQL database
     * @throws  SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Safely closes a database connection.
     * Suppresses exceptions to avoid masking the original exception
     * in calling code.
     *
     * @param conn  The {@link Connection} to close (may be null)
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DBConnection] Warning: Failed to close connection.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests the database connection.
     * Useful during deployment to verify configuration.
     *
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DBConnection] Connection test failed: " + e.getMessage());
            return false;
        }
    }
}