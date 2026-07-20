package com.realestate.dao;

import com.realestate.model.*;
import com.realestate.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PropertyDAO - Data Access Object for the properties table.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class PropertyDAO {

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------

    /**
     * Inserts a new property listing with status PENDING.
     * Returns the generated property_id, or -1 on failure.
     */
    public int createProperty(Property p) {
        String sql = "INSERT INTO properties " +
                     "(owner_id, type_id, category_id, title, description, price, " +
                     " bedrooms, bathrooms, size_sqm, furnishing_status, campus_distance, " +
                     " water_availability, electricity, security_level, internet_available, " +
                     " road_accessibility, is_fenced, property_status, cover_image) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'PENDING',?) " +
                     "RETURNING property_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, p.getOwnerId());
            ps.setInt(2, p.getTypeId());
            ps.setInt(3, p.getCategoryId());
            ps.setString(4, p.getTitle());
            ps.setString(5, p.getDescription());
            ps.setBigDecimal(6, p.getPrice());
            ps.setInt(7, p.getBedrooms());
            ps.setInt(8, p.getBathrooms());
            
            if (p.getSizeSqm() != null) {
                ps.setBigDecimal(9, p.getSizeSqm());
            } else {
                ps.setNull(9, Types.NUMERIC);
            }

            ps.setString(10, p.getFurnishingStatus());
            ps.setString(11, p.getCampusDistance());
            ps.setString(12, p.getWaterAvailability());
            ps.setString(13, p.getElectricity());
            ps.setString(14, p.getSecurityLevel());
            ps.setBoolean(15, p.isInternetAvailable());
            ps.setString(16, p.getRoadAccessibility());
            ps.setBoolean(17, p.isFenced());
            ps.setString(18, p.getCoverImage());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("property_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.createProperty] Error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Inserts a property location record linked to the given property.
     */
    public boolean createLocation(PropertyLocation loc) {
        String sql = "INSERT INTO property_locations " +
                     "(property_id, area_name, street_address, landmark, lga, state) " +
                     "VALUES (?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, loc.getPropertyId());
            ps.setString(2, loc.getAreaName());
            ps.setString(3, loc.getStreetAddress());
            ps.setString(4, loc.getLandmark());
            ps.setString(5, loc.getLga());
            ps.setString(6, loc.getState());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.createLocation] Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Inserts a single property image record.
     */
    public boolean addImage(PropertyImage img) {
        String sql = "INSERT INTO property_images " +
                     "(property_id, image_path, image_caption, is_cover, display_order) " +
                     "VALUES (?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, img.getPropertyId());
            ps.setString(2, img.getImagePath());
            ps.setString(3, img.getImageCaption());
            ps.setBoolean(4, img.isCover());
            ps.setInt(5, img.getDisplayOrder());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.addImage] Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Associates an amenity with a property.
     */
    public boolean addAmenity(int propertyId, int amenityId) {
        String sql = "INSERT INTO property_amenities (property_id, amenity_id) " +
                     "VALUES (?, ?) ON CONFLICT (property_id, amenity_id) DO NOTHING";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, propertyId);
            ps.setInt(2, amenityId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.addAmenity] Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // READ - Single Property
    // ---------------------------------------------------------------

    /**
     * Retrieves a single property by its primary key.
     */
    public Property findById(int propertyId) {
        String sql = "SELECT p.*, pt.type_name, pc.category_name, " +
                     "       u.full_name AS owner_name, u.email AS owner_email, " +
                     "       u.phone AS owner_phone, u.profile_photo AS owner_photo, " +
                     "       pl.area_name, pl.street_address, pl.landmark, pl.lga, pl.state, pl.location_id, " +
                     "       COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, " +
                     "       COUNT(DISTINCT pr.rating_id) AS total_ratings " +
                     "FROM properties p " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
                     "WHERE p.property_id = ? " +
                     "GROUP BY p.property_id, pt.type_name, pc.category_name, " +
                     "         u.full_name, u.email, u.phone, u.profile_photo, " +
                     "         pl.area_name, pl.street_address, pl.landmark, " +
                     "         pl.lga, pl.state, pl.location_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Property prop = mapRow(rs);
                    prop.setImages(findImagesByProperty(propertyId));
                    prop.setAmenities(findAmenitiesByProperty(propertyId));
                    return prop;
                }
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.findById] Error: " + e.getMessage());
        }
        return null;
    }

    // ---------------------------------------------------------------
    // READ - Search and Filter
    // ---------------------------------------------------------------

    /**
     * Searches properties using dynamic filter parameters.
     */
    public List<Property> searchProperties(String keyword, int typeId, int categoryId, String campusDistance,
                                           BigDecimal minPrice, BigDecimal maxPrice, String sortBy, int page, int pageSize) {

        StringBuilder sql = new StringBuilder(
            "SELECT p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.email AS owner_email, u.phone AS owner_phone, " +
            "       pl.area_name, pl.lga, COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, COUNT(DISTINCT pr.rating_id) AS total_ratings " +
            "FROM properties p " +
            "JOIN property_types pt ON p.type_id = pt.type_id " +
            "JOIN property_categories pc ON p.category_id = pc.category_id " +
            "JOIN users u ON p.owner_id = u.user_id " +
            "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
            "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
            "WHERE p.property_status = 'AVAILABLE' "
        );
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (to_tsvector('english', p.title || ' ' || p.description) @@ plainto_tsquery('english', ?) ")
               .append("OR LOWER(p.title) LIKE LOWER(?)) ");
            params.add(keyword.trim());
            params.add("%" + keyword.trim() + "%");
        }
        if (typeId > 0) {
            sql.append("AND p.type_id = ? ");
            params.add(typeId);
        }
        if (categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (campusDistance != null && !campusDistance.isEmpty()) {
            sql.append("AND p.campus_distance = ? ");
            params.add(campusDistance);
        }
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }

        sql.append("GROUP BY p.property_id, pt.type_name, pc.category_name, u.full_name, u.email, u.phone, pl.area_name, pl.lga ");

        switch (sortBy != null ? sortBy : "date") {
            case "price_asc":  sql.append("ORDER BY p.price ASC "); break;
            case "price_desc": sql.append("ORDER BY p.price DESC "); break;
            case "popular":    sql.append("ORDER BY (p.view_count + p.save_count * 3 + p.inquiry_count * 5) DESC "); break;
            case "rating":     sql.append("ORDER BY avg_rating DESC "); break;
            default:           sql.append("ORDER BY p.created_at DESC ");
        }

        int offset = (page - 1) * pageSize;
        sql.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        List<Property> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
            return results;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.searchProperties] Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Counts the total number of results matching the same search criteria.
     */
    public int countSearchResults(String keyword, int typeId, int categoryId, String campusDistance, BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM properties p WHERE p.property_status = 'AVAILABLE' ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (to_tsvector('english', p.title || ' ' || p.description) @@ plainto_tsquery('english', ?) ")
               .append("OR LOWER(p.title) LIKE LOWER(?)) ");
            params.add(keyword.trim());
            params.add("%" + keyword.trim() + "%");
        }
        if (typeId > 0) {
            sql.append("AND p.type_id = ? ");
            params.add(typeId);
        }
        if (categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (campusDistance != null && !campusDistance.isEmpty()) {
            sql.append("AND p.campus_distance = ? ");
            params.add(campusDistance);
        }
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.countSearchResults] Error: " + e.getMessage());
        }
        return 0;
    }

    public List<Property> findFeatured(int limit) {
        String sql = "SELECT p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.phone AS owner_phone, pl.area_name, pl.lga, " +
                     "       COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, COUNT(DISTINCT pr.rating_id) AS total_ratings " +
                     "FROM properties p " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
                     "WHERE p.property_status = 'AVAILABLE' AND p.is_featured = TRUE " +
                     "GROUP BY p.property_id, pt.type_name, pc.category_name, u.full_name, u.phone, pl.area_name, pl.lga " +
                     "ORDER BY p.created_at DESC LIMIT ?";
        return executeListQuery(sql, new Object[]{limit});
    }

    public List<Property> findPopular(int limit) {
        String sql = "SELECT p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.phone AS owner_phone, pl.area_name, pl.lga, " +
                     "       COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, COUNT(DISTINCT pr.rating_id) AS total_ratings " +
                     "FROM properties p " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
                     "WHERE p.property_status = 'AVAILABLE' " +
                     "GROUP BY p.property_id, pt.type_name, pc.category_name, u.full_name, u.phone, pl.area_name, pl.lga " +
                     "ORDER BY (p.view_count + p.save_count * 3 + p.inquiry_count * 5) DESC LIMIT ?";
        return executeListQuery(sql, new Object[]{limit});
    }

    public List<Property> findByOwner(int ownerId) {
        String sql = "SELECT p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.phone AS owner_phone, pl.area_name, pl.lga, " +
                     "       COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, COUNT(DISTINCT pr.rating_id) AS total_ratings " +
                     "FROM properties p " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
                     "WHERE p.owner_id = ? " +
                     "GROUP BY p.property_id, pt.type_name, pc.category_name, u.full_name, u.phone, pl.area_name, pl.lga " +
                     "ORDER BY p.created_at DESC";
        return executeListQuery(sql, new Object[]{ownerId});
    }

    public List<Property> findPending() {
        String sql = "SELECT p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.phone AS owner_phone, u.email AS owner_email, pl.area_name, pl.lga, " +
                     "       COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, COUNT(DISTINCT pr.rating_id) AS total_ratings " +
                     "FROM properties p " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
                     "WHERE p.property_status = 'PENDING' " +
                     "GROUP BY p.property_id, pt.type_name, pc.category_name, u.full_name, u.phone, u.email, pl.area_name, pl.lga " +
                     "ORDER BY p.created_at ASC";
        return executeListQuery(sql, new Object[]{});
    }

    public List<Property> findSavedByUser(int userId) {
        String sql = "SELECT p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.phone AS owner_phone, pl.area_name, pl.lga, " +
                     "       COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, COUNT(DISTINCT pr.rating_id) AS total_ratings " +
                     "FROM saved_properties sp " +
                     "JOIN properties p ON sp.property_id = p.property_id " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
                     "WHERE sp.user_id = ? " +
                     "GROUP BY p.property_id, pt.type_name, pc.category_name, u.full_name, u.phone, pl.area_name, pl.lga, sp.saved_at " +
                     "ORDER BY sp.saved_at DESC";
        return executeListQuery(sql, new Object[]{userId});
    }

    public List<Property> findRecentlyViewedByUser(int userId) {
        String sql = "SELECT DISTINCT ON (p.property_id) p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.phone AS owner_phone, " +
                     "       pl.area_name, pl.lga, 0.0 AS avg_rating, 0 AS total_ratings, rv.viewed_at " +
                     "FROM recently_viewed_properties rv " +
                     "JOIN properties p ON rv.property_id = p.property_id " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "WHERE rv.user_id = ? ORDER BY p.property_id, rv.viewed_at DESC LIMIT 10";
        return executeListQuery(sql, new Object[]{userId});
    }

    public List<Property> getSuggestions(String query) {
        String sql = "SELECT p.property_id, p.title, pl.area_name, '' AS type_name, '' AS category_name, " +
                     "       '' AS owner_name, '' AS owner_phone, '' AS owner_email, '' AS lga, 0.0 AS avg_rating, 0 AS total_ratings " +
                     "FROM properties p " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "WHERE p.property_status = 'AVAILABLE' AND LOWER(p.title) LIKE LOWER(?) LIMIT 6";
        return executeListQuery(sql, new Object[]{"%" + query.trim() + "%"});
    }

    public List<Property> findSimilar(int propertyId, int typeId, BigDecimal price) {
        String sql = "SELECT p.*, pt.type_name, pc.category_name, u.full_name AS owner_name, u.phone AS owner_phone, pl.area_name, pl.lga, " +
                     "       COALESCE(ROUND(AVG(pr.rating_value),1), 0) AS avg_rating, COUNT(DISTINCT pr.rating_id) AS total_ratings " +
                     "FROM properties p " +
                     "JOIN property_types pt ON p.type_id = pt.type_id " +
                     "JOIN property_categories pc ON p.category_id = pc.category_id " +
                     "JOIN users u ON p.owner_id = u.user_id " +
                     "LEFT JOIN property_locations pl ON p.property_id = pl.property_id " +
                     "LEFT JOIN property_ratings pr ON p.property_id = pr.property_id " +
                     "WHERE p.property_status = 'AVAILABLE' AND p.type_id = ? AND p.property_id != ? AND p.price BETWEEN ? AND ? " +
                     "GROUP BY p.property_id, pt.type_name, pc.category_name, u.full_name, u.phone, pl.area_name, pl.lga " +
                     "ORDER BY ABS(p.price - ?) ASC LIMIT 4";

        BigDecimal lower = price.multiply(new BigDecimal("0.7"));
        BigDecimal upper = price.multiply(new BigDecimal("1.3"));
        return executeListQuery(sql, new Object[]{typeId, propertyId, lower, upper, price});
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------

    public boolean updateProperty(Property p) {
        String sql = "UPDATE properties SET type_id = ?, category_id = ?, title = ?, description = ?, " +
                     "price = ?, bedrooms = ?, bathrooms = ?, size_sqm = ?, furnishing_status = ?, campus_distance = ?, " +
                     "water_availability = ?, electricity = ?, security_level = ?, internet_available = ?, road_accessibility = ?, is_fenced = ?, " +
                     "updated_at = NOW() WHERE property_id = ? AND owner_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, p.getTypeId());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getTitle());
            ps.setString(4, p.getDescription());
            ps.setBigDecimal(5, p.getPrice());
            ps.setInt(6, p.getBedrooms());
            ps.setInt(7, p.getBathrooms());

            if (p.getSizeSqm() != null) {
                ps.setBigDecimal(8, p.getSizeSqm());
            } else {
                ps.setNull(8, Types.NUMERIC);
            }

            ps.setString(9, p.getFurnishingStatus());
            ps.setString(10, p.getCampusDistance());
            ps.setString(11, p.getWaterAvailability());
            ps.setString(12, p.getElectricity());
            ps.setString(13, p.getSecurityLevel());
            ps.setBoolean(14, p.isInternetAvailable());
            ps.setString(15, p.getRoadAccessibility());
            ps.setBoolean(16, p.isFenced());
            ps.setInt(17, p.getPropertyId());
            ps.setInt(18, p.getOwnerId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.updateProperty] Error: " + e.getMessage());
        }
        return false;
    }

    public boolean approveProperty(int propertyId, int adminId) {
        String sql = "UPDATE properties SET property_status = 'AVAILABLE', is_verified = TRUE, verification_status = 'VERIFIED', " +
                     "approved_by = ?, approved_at = NOW(), updated_at = NOW() WHERE property_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.approveProperty] Error: " + e.getMessage());
        }
        return false;
    }

    public boolean rejectProperty(int propertyId, int adminId) {
        String sql = "UPDATE properties SET property_status = 'SUSPENDED', verification_status = 'REJECTED', " +
                     "approved_by = ?, updated_at = NOW() WHERE property_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.rejectProperty] Error: " + e.getMessage());
        }
        return false;
    }

    public boolean setFeatured(int propertyId, boolean isFeatured) {
        String sql = "UPDATE properties SET is_featured = ?, updated_at = NOW() WHERE property_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isFeatured);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.setFeatured] Error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStatus(int propertyId, String status) {
        String sql = "UPDATE properties SET property_status = ?, updated_at = NOW() WHERE property_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, propertyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.updateStatus] Error: " + e.getMessage());
        }
        return false;
    }

    public void incrementViewCount(int propertyId, int userId, String ipAddress) {
        String updateSql = "UPDATE properties SET view_count = view_count + 1 WHERE property_id = ?";
        String insertSql = "INSERT INTO property_views (property_id, viewer_ip, user_id) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateSql);
                 PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                
                ps1.setInt(1, propertyId);
                ps1.executeUpdate();

                ps2.setInt(1, propertyId);
                ps2.setString(2, ipAddress);
                if (userId > 0) {
                    ps2.setInt(3, userId);
                } else {
                    ps2.setNull(3, Types.INTEGER);
                }
                ps2.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    try { conn.rollback(); } catch (SQLException ignored) {}
                }
                throw e;
            } finally {
                if (conn != null) {
                    try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                }
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.incrementViewCount] Transaction Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // ---------------------------------------------------------------
    // WISHLIST
    // ---------------------------------------------------------------

    public String toggleWishlist(int userId, int propertyId) {
        String checkSql = "SELECT 1 FROM saved_properties WHERE user_id=? AND property_id=?";
        String deleteSql = "DELETE FROM saved_properties WHERE user_id=? AND property_id=?";
        String insertSql = "INSERT INTO saved_properties (user_id, property_id) VALUES (?,?)";
        String decrSql = "UPDATE properties SET save_count = GREATEST(save_count-1, 0) WHERE property_id=?";
        String incrSql = "UPDATE properties SET save_count = save_count+1 WHERE property_id=?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                checkPs.setInt(2, propertyId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                try (PreparedStatement del = conn.prepareStatement(deleteSql);
                     PreparedStatement decr = conn.prepareStatement(decrSql)) {
                    del.setInt(1, userId);
                    del.setInt(2, propertyId);
                    del.executeUpdate();

                    decr.setInt(1, propertyId);
                    decr.executeUpdate();
                    
                    conn.commit();
                    return "removed";
                }
            } else {
                try (PreparedStatement ins = conn.prepareStatement(insertSql);
                     PreparedStatement incr = conn.prepareStatement(incrSql)) {
                    ins.setInt(1, userId);
                    ins.setInt(2, propertyId);
                    ins.executeUpdate();

                    incr.setInt(1, propertyId);
                    incr.executeUpdate();
                    
                    conn.commit();
                    return "saved";
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            System.err.println("[PropertyDAO.toggleWishlist] Transaction Error: " + e.getMessage());
            return "error";
        } finally {
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch (SQLException ignored) {}
            }
        }
    }

    public boolean isSavedByUser(int userId, int propertyId) {
        String sql = "SELECT 1 FROM saved_properties WHERE user_id=? AND property_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.isSavedByUser] Error: " + e.getMessage());
        }
        return false;
    }

    public void recordRecentlyViewed(int userId, int propertyId) {
        String sql = "INSERT INTO recently_viewed_properties (user_id, property_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, propertyId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.recordRecentlyViewed] Error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    public boolean deleteProperty(int propertyId, int ownerId) {
        String sql = (ownerId > 0) ? "DELETE FROM properties WHERE property_id = ? AND owner_id = ?" 
                                   : "DELETE FROM properties WHERE property_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, propertyId);
            if (ownerId > 0) {
                ps.setInt(2, ownerId);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.deleteProperty] Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // LOOKUP HELPERS - Types, Categories, Amenities, Images
    // ---------------------------------------------------------------

    public List<PropertyType> findAllTypes() {
        String sql = "SELECT * FROM property_types ORDER BY type_name";
        List<PropertyType> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PropertyType pt = new PropertyType();
                pt.setTypeId(rs.getInt("type_id"));
                pt.setTypeName(rs.getString("type_name"));
                pt.setTypeIcon(rs.getString("type_icon"));
                list.add(pt);
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.findAllTypes] Error: " + e.getMessage());
        }
        return list;
    }

    public List<PropertyCategory> findAllCategories() {
        String sql = "SELECT * FROM property_categories ORDER BY category_name";
        List<PropertyCategory> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PropertyCategory pc = new PropertyCategory();
                pc.setCategoryId(rs.getInt("category_id"));
                pc.setCategoryName(rs.getString("category_name"));
                list.add(pc);
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.findAllCategories] Error: " + e.getMessage());
        }
        return list;
    }

    public List<Amenity> findAllAmenities() {
        String sql = "SELECT * FROM amenities ORDER BY amenity_name";
        List<Amenity> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Amenity a = new Amenity();
                a.setAmenityId(rs.getInt("amenity_id"));
                a.setAmenityName(rs.getString("amenity_name"));
                a.setAmenityIcon(rs.getString("amenity_icon"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.findAllAmenities] Error: " + e.getMessage());
        }
        return list;
    }

    public List<PropertyImage> findImagesByProperty(int propertyId) {
        String sql = "SELECT * FROM property_images WHERE property_id = ? ORDER BY is_cover DESC, display_order ASC";
        List<PropertyImage> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PropertyImage img = new PropertyImage();
                    img.setImageId(rs.getInt("image_id"));
                    img.setPropertyId(rs.getInt("property_id"));
                    img.setImagePath(rs.getString("image_path"));
                    img.setImageCaption(rs.getString("image_caption"));
                    img.setCover(rs.getBoolean("is_cover"));
                    img.setDisplayOrder(rs.getInt("display_order"));
                    img.setUploadedAt(rs.getTimestamp("uploaded_at"));
                    list.add(img);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.findImagesByProperty] Error: " + e.getMessage());
        }
        return list;
    }

    public List<Amenity> findAmenitiesByProperty(int propertyId) {
        String sql = "SELECT a.* FROM amenities a " +
                     "JOIN property_amenities pa ON a.amenity_id = pa.amenity_id " +
                     "WHERE pa.property_id = ? ORDER BY a.amenity_name";
        List<Amenity> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Amenity a = new Amenity();
                    a.setAmenityId(rs.getInt("amenity_id"));
                    a.setAmenityName(rs.getString("amenity_name"));
                    a.setAmenityIcon(rs.getString("amenity_icon"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.findAmenitiesByProperty] Error: " + e.getMessage());
        }
        return list;
    }

    public boolean clearAmenities(int propertyId) {
        String sql = "DELETE FROM property_amenities WHERE property_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, propertyId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.clearAmenities] Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // ADMIN STATS
    // ---------------------------------------------------------------

    public int countTotal() {
        return countBySql("SELECT COUNT(*) FROM properties");
    }

    public int countAvailable() {
        return countBySql("SELECT COUNT(*) FROM properties WHERE property_status='AVAILABLE'");
    }

    public int countPending() {
        return countBySql("SELECT COUNT(*) FROM properties WHERE property_status='PENDING'");
    }

    public int countVerified() {
        return countBySql("SELECT COUNT(*) FROM properties WHERE is_verified=TRUE");
    }

    private int countBySql(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.countBySql] Error: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------------------------------------------
    // PRIVATE HELPERS
    // ---------------------------------------------------------------

    private List<Property> executeListQuery(String sql, Object[] params) {
        List<Property> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PropertyDAO.executeListQuery] Error: " + e.getMessage());
        }
        return list;
    }

    private Property mapRow(ResultSet rs) throws SQLException {
        Property p = new Property();
        p.setPropertyId(rs.getInt("property_id"));
        p.setOwnerId(rs.getInt("owner_id"));
        p.setTypeId(rs.getInt("type_id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setBedrooms(rs.getInt("bedrooms"));
        p.setBathrooms(rs.getInt("bathrooms"));
        p.setSizeSqm(rs.getBigDecimal("size_sqm"));
        p.setFurnishingStatus(rs.getString("furnishing_status"));
        p.setCampusDistance(rs.getString("campus_distance"));
        p.setWaterAvailability(rs.getString("water_availability"));
        p.setElectricity(rs.getString("electricity"));
        p.setSecurityLevel(rs.getString("security_level"));
        p.setInternetAvailable(rs.getBoolean("internet_available"));
        p.setRoadAccessibility(rs.getString("road_accessibility"));
        p.setFenced(rs.getBoolean("is_fenced"));
        p.setViewCount(rs.getInt("view_count"));
        p.setSaveCount(rs.getInt("save_count"));
        p.setInquiryCount(rs.getInt("inquiry_count"));
        p.setPropertyStatus(rs.getString("property_status"));
        p.setVerified(rs.getBoolean("is_verified"));
        p.setVerificationStatus(rs.getString("verification_status"));
        p.setCoverImage(rs.getString("cover_image"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        try { p.setTypeName(rs.getString("type_name"));         } catch (SQLException ignored) {}
        try { p.setCategoryName(rs.getString("category_name")); } catch (SQLException ignored) {}
        try { p.setOwnerName(rs.getString("owner_name"));       } catch (SQLException ignored) {}
        try { p.setOwnerEmail(rs.getString("owner_email"));     } catch (SQLException ignored) {}
        try { p.setOwnerPhone(rs.getString("owner_phone"));     } catch (SQLException ignored) {}

        try {
            String areaName = rs.getString("area_name");
            if (areaName != null) {
                PropertyLocation loc = new PropertyLocation();
                loc.setAreaName(areaName);
                try { loc.setStreetAddress(rs.getString("street_address")); } catch (SQLException ignored) {}
                try { loc.setLandmark(rs.getString("landmark"));            } catch (SQLException ignored) {}
                try { loc.setLga(rs.getString("lga"));                      } catch (SQLException ignored) {}
                p.setLocation(loc);
            }
        } catch (SQLException ignored) {}

        try { p.setAverageRating(rs.getDouble("avg_rating"));   } catch (SQLException ignored) {}
        try { p.setTotalRatings(rs.getInt("total_ratings"));    } catch (SQLException ignored) {}

        return p;
    }
}