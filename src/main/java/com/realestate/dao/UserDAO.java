package com.realestate.dao;

import com.realestate.model.User;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UserDAO - Data Access Object for the users table.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class UserDAO {

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------

    /**
     * Inserts a new user record into the users table.
     * Returns the generated user_id, or -1 on failure.
     *
     * @param user  Populated User object (passwordHash must already be BCrypt hash)
     * @return      Generated user_id, or -1 if insert failed
     */
    public int createUser(User user) {
        String sql = "INSERT INTO users " +
                     "(role_id, full_name, email, password_hash, phone, " +
                     " account_status, is_verified) " +
                     "VALUES (?, ?, ?, ?, ?, 'ACTIVE', FALSE) " +
                     "RETURNING user_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail().toLowerCase().trim());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getPhone());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.createUser] Production Error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Inserts a basic user_profile record linked to the given userId.
     * Called immediately after createUser() during registration.
     *
     * @param userId  The newly created user's ID
     * @param gender  Gender string: MALE, FEMALE, or OTHER
     * @return        true if insert succeeded
     */
    public boolean createUserProfile(int userId, String gender) {
        String sql = "INSERT INTO user_profiles (user_id, gender) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, gender);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.createUserProfile] Production Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // READ - Single User
    // ---------------------------------------------------------------

    /**
     * Retrieves a user by primary key.
     * Joins roles table to populate roleName field.
     *
     * @param  userId  The user's primary key
     * @return         Populated User object, or null if not found
     */
    public User findById(int userId) {
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.findById] Production Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a user by email address (case-insensitive).
     * Used during login and registration duplicate check.
     *
     * @param  email  The email address to look up
     * @return        Populated User object, or null if not found
     */
    public User findByEmail(String email) {
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE LOWER(u.email) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.findByEmail] Production Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a user by their password reset token.
     * Used during the forgot-password reset workflow.
     *
     * @param  token  The reset token string
     * @return        Populated User object, or null if token not found or expired
     */
    public User findByResetToken(String token) {
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.reset_token = ? " +
                     "AND u.reset_token_expiry > NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.findByResetToken] Production Error: " + e.getMessage());
        }
        return null;
    }

    // ---------------------------------------------------------------
    // READ - Collections
    // ---------------------------------------------------------------

    /**
     * Returns all users, optionally filtered by role name.
     *
     * @param  roleFilter  Role name to filter by (e.g. "STUDENT"), or null for all
     * @return             List of matching User objects
     */
    public List<User> findAll(String roleFilter) {
        StringBuilder sql = new StringBuilder(
            "SELECT u.*, r.role_name " +
            "FROM users u " +
            "JOIN roles r ON u.role_id = r.role_id "
        );

        List<Object> params = new ArrayList<>();
        if (roleFilter != null && !roleFilter.isEmpty()) {
            sql.append("WHERE r.role_name = ? ");
            params.add(roleFilter);
        }
        sql.append("ORDER BY u.created_at DESC");

        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            System.err.println("[UserDAO.findAll] Production Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Searches users by keyword (name or email).
     * Used in admin user management search.
     *
     * @param  keyword  The search term
     * @return          List of matching users
     */
    public List<User> searchUsers(String keyword) {
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE LOWER(u.full_name) LIKE LOWER(?) " +
                     "OR LOWER(u.email) LIKE LOWER(?) " +
                     "ORDER BY u.full_name";

        String like = "%" + keyword.trim() + "%";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, like);
            ps.setString(2, like);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            System.err.println("[UserDAO.searchUsers] Production Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------

    /**
     * Updates the user's full name, phone, and profile photo path.
     *
     * @param  userId        Target user's ID
     * @param  fullName      Updated full name
     * @param  phone         Updated phone number
     * @param  profilePhoto  Updated profile photo path (null to keep existing)
     * @return               true if update succeeded
     */
    public boolean updateProfile(int userId, String fullName, String phone, String profilePhoto) {
        String sql;
        boolean hasPhoto = (profilePhoto != null && !profilePhoto.isEmpty());

        if (hasPhoto) {
            sql = "UPDATE users SET full_name = ?, phone = ?, profile_photo = ?, updated_at = NOW() WHERE user_id = ?";
        } else {
            sql = "UPDATE users SET full_name = ?, phone = ?, updated_at = NOW() WHERE user_id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, fullName);
            ps.setString(2, phone);
            if (hasPhoto) {
                ps.setString(3, profilePhoto);
                ps.setInt(4, userId);
            } else {
                ps.setInt(3, userId);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.updateProfile] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates a user's password hash in the database.
     * Called after a successful password reset.
     *
     * @param  userId       Target user's ID
     * @param  newHashedPw  New BCrypt hash
     * @return              true if update succeeded
     */
    public boolean updatePassword(int userId, String newHashedPw) {
        String sql = "UPDATE users SET password_hash = ?, " +
                     "reset_token = NULL, reset_token_expiry = NULL, " +
                     "updated_at = NOW() WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newHashedPw);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.updatePassword] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Saves a password reset token and its expiry timestamp.
     *
     * @param  userId        Target user's ID
     * @param  token         The generated reset token
     * @param  expiryMinutes Minutes until the token expires (e.g. 60)
     * @return               true if update succeeded
     */
    public boolean saveResetToken(int userId, String token, int expiryMinutes) {
        String sql = "UPDATE users SET reset_token = ?, " +
                     "reset_token_expiry = NOW() + INTERVAL '" + expiryMinutes + " minutes' " +
                     "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, token);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.saveResetToken] Production Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates the user's last_login timestamp to the current time.
     *
     * @param  userId  Target user's ID
     */
    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO.updateLastLogin] Production Error: " + e.getMessage());
        }
    }

    /**
     * Updates a user's account status.
     * Admin-only operation. Status values: ACTIVE, SUSPENDED, BLACKLISTED
     *
     * @param  userId  Target user's ID
     * @param  status  New account status string
     * @return         true if update succeeded
     */
    public boolean updateAccountStatus(int userId, String status) {
        String sql = "UPDATE users SET account_status = ?, updated_at = NOW() " +
                     "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.updateAccountStatus] Production Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // CHECKS
    // ---------------------------------------------------------------

    /**
     * Returns true if an account with the given email already exists.
     * Used during registration to prevent duplicate accounts.
     *
     * @param  email  The email to check
     * @return        true if a user with this email already exists
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.emailExists] Production Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // ADMIN STATISTICS
    // ---------------------------------------------------------------

    /**
     * Returns the total count of users with the given role name.
     *
     * @param  roleName  The role to count (e.g. "STUDENT")
     * @return           Count of users with that role
     */
    public int countByRole(String roleName) {
        String sql = "SELECT COUNT(*) FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE r.role_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.countByRole] Production Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns the total number of registered users.
     *
     * @return  Total user count
     */
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO.countTotal] Production Error: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------------------------------------------
    // ROW MAPPER
    // ---------------------------------------------------------------

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setRoleId(rs.getInt("role_id"));
        user.setRoleName(rs.getString("role_name"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setProfilePhoto(rs.getString("profile_photo"));
        user.setAccountStatus(rs.getString("account_status"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setResetToken(rs.getString("reset_token"));
        user.setResetTokenExpiry(rs.getTimestamp("reset_token_expiry"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}