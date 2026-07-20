package com.realestate.dao;

import com.realestate.model.Appointment;
import com.realestate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AppointmentDAO - Data Access Object for the appointments table.
 * Fully optimized for resource closure safety and parameterized execution.
 *
 * @author  AOPE CS Department
 * @version 1.1
 */
public class AppointmentDAO {

    /**
     * Creates a new viewing appointment request.
     *
     * @param  appt  Populated Appointment object
     * @return       Generated appointment_id, or -1 on failure
     */
    public int createAppointment(Appointment appt) {
        String sql = "INSERT INTO appointments " +
                     "(property_id, requester_id, owner_id, preferred_date, preferred_time, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING appointment_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, appt.getPropertyId());
            ps.setInt(2, appt.getRequesterId());
            ps.setInt(3, appt.getOwnerId());
            ps.setDate(4, appt.getPreferredDate());

            if (appt.getPreferredTime() != null) {
                ps.setTime(5, appt.getPreferredTime());
            } else {
                ps.setNull(5, Types.TIME);
            }

            ps.setString(6, appt.getNotes());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("appointment_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO.createAppointment] Production Error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Returns all appointments for properties owned by a specific user.
     *
     * @param  ownerId  The property owner's user_id
     * @return          List of Appointment objects
     */
    public List<Appointment> findByOwner(int ownerId) {
        String sql = "SELECT a.*, " +
                     "       u.full_name AS requester_name, " +
                     "       u.phone AS requester_phone, " +
                     "       p.title AS property_title " +
                     "FROM appointments a " +
                     "JOIN users u ON a.requester_id = u.user_id " +
                     "JOIN properties p ON a.property_id = p.property_id " +
                     "WHERE a.owner_id = ? " +
                     "ORDER BY a.preferred_date ASC, a.preferred_time ASC";

        return executeListQuery(sql, ownerId);
    }

    /**
     * Returns all appointments requested by a specific user.
     *
     * @param  requesterId  The requester's user_id
     * @return              List of Appointment objects
     */
    public List<Appointment> findByRequester(int requesterId) {
        String sql = "SELECT a.*, " +
                     "       p.title AS property_title, " +
                     "       u.full_name AS owner_name, " +
                     "       '' AS requester_name, '' AS requester_phone " +
                     "FROM appointments a " +
                     "JOIN properties p ON a.property_id = p.property_id " +
                     "JOIN users u ON a.owner_id = u.user_id " +
                     "WHERE a.requester_id = ? " +
                     "ORDER BY a.preferred_date DESC";

        return executeListQuery(sql, requesterId);
    }

    /**
     * Updates the status of an appointment.
     *
     * @param  appointmentId  Target appointment
     * @param  status         New status: CONFIRMED, CANCELLED, COMPLETED
     * @return                true if update succeeded
     */
    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET appointment_status = ?, updated_at = NOW() " +
                     "WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO.updateStatus] Production Error: " + e.getMessage());
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Structural Data Helpers
    // ---------------------------------------------------------------

    /**
     * Executes queries using precise bound tracking items.
     */
    private List<Appointment> executeListQuery(String sql, int targetId) {
        List<Appointment> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, targetId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO.executeListQuery] Production Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Maps database column vectors smoothly back to model parameters.
     */
    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPropertyId(rs.getInt("property_id"));
        a.setRequesterId(rs.getInt("requester_id"));
        a.setOwnerId(rs.getInt("owner_id"));
        a.setPreferredDate(rs.getDate("preferred_date"));
        a.setPreferredTime(rs.getTime("preferred_time"));
        a.setAppointmentStatus(rs.getString("appointment_status"));
        a.setNotes(rs.getString("notes"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Defensive mapping adjustments for extended entity details
        try { a.setRequesterName(rs.getString("requester_name")); } catch (SQLException ignored) {}
        try { a.setRequesterPhone(rs.getString("requester_phone")); } catch (SQLException ignored) {}
        try { a.setPropertyTitle(rs.getString("property_title")); } catch (SQLException ignored) {}
        try { a.setOwnerName(rs.getString("owner_name"));         } catch (SQLException ignored) {}
        return a;
    }
}