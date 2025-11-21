package com.hotel.repository;

import com.hotel.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Reservation> RESERVATION_ROW_MAPPER = new RowMapper<Reservation>() {
        @Override
        public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Reservation reservation = new Reservation();
            reservation.setId(rs.getInt("id"));
            reservation.setGuestName(rs.getString("guest_name"));
            reservation.setGuestEmail(rs.getString("guest_email"));
            reservation.setPhone(rs.getString("phone"));
            reservation.setRoomTypeId(rs.getInt("room_type_id"));
            reservation.setCheckInDate(rs.getObject("check_in_date", LocalDate.class));
            reservation.setCheckOutDate(rs.getObject("check_out_date", LocalDate.class));
            reservation.setStatus(rs.getString("status"));
            Integer createdBy = rs.getObject("created_by", Integer.class);
            reservation.setCreatedBy(createdBy);
            reservation.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return reservation;
        }
    };

    public Reservation create(Reservation reservation) {
        String sql = "INSERT INTO reservations (guest_name, guest_email, phone, room_type_id, check_in_date, check_out_date, status, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            reservation.setId(rs.getInt("id"));
            reservation.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return reservation;
        }, reservation.getGuestName(), reservation.getGuestEmail(), reservation.getPhone(), 
           reservation.getRoomTypeId(), reservation.getCheckInDate(), reservation.getCheckOutDate(), 
           reservation.getStatus(), reservation.getCreatedBy());
    }

    public Optional<Reservation> findById(Integer id) {
        String sql = "SELECT id, guest_name, guest_email, phone, room_type_id, check_in_date, check_out_date, status, created_by, created_at " +
                     "FROM reservations WHERE id = ?";
        try {
            Reservation reservation = jdbcTemplate.queryForObject(sql, RESERVATION_ROW_MAPPER, id);
            return Optional.ofNullable(reservation);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Reservation> findByDateRange(LocalDate from, LocalDate to, String status) {
        String sql = "SELECT id, guest_name, guest_email, phone, room_type_id, check_in_date, check_out_date, status, created_by, created_at " +
                     "FROM reservations WHERE check_in_date <= ? AND check_out_date >= ? AND status = ? ORDER BY check_in_date";
        return jdbcTemplate.query(sql, RESERVATION_ROW_MAPPER, to, from, status);
    }

    public List<Reservation> findAll(String status) {
        if (status != null && !status.trim().isEmpty()) {
            String sql = "SELECT id, guest_name, guest_email, phone, room_type_id, check_in_date, check_out_date, status, created_by, created_at " +
                         "FROM reservations WHERE status = ? ORDER BY created_at DESC";
            return jdbcTemplate.query(sql, RESERVATION_ROW_MAPPER, status);
        } else {
            String sql = "SELECT id, guest_name, guest_email, phone, room_type_id, check_in_date, check_out_date, status, created_by, created_at " +
                         "FROM reservations ORDER BY created_at DESC";
            return jdbcTemplate.query(sql, RESERVATION_ROW_MAPPER);
        }
    }

    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET guest_name = ?, guest_email = ?, phone = ?, room_type_id = ?, " +
                     "check_in_date = ?, check_out_date = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(sql, reservation.getGuestName(), reservation.getGuestEmail(), reservation.getPhone(),
                           reservation.getRoomTypeId(), reservation.getCheckInDate(), reservation.getCheckOutDate(),
                           reservation.getStatus(), reservation.getId());
    }

    // Check availability with SELECT FOR UPDATE for concurrency control
    public int countOverlappingReservations(Integer roomTypeId, LocalDate checkIn, LocalDate checkOut, Integer excludeReservationId) {
        // First lock the rows, then count (FOR UPDATE can't be used with COUNT)
        StringBuilder lockSql = new StringBuilder("SELECT id FROM reservations " +
                        "WHERE room_type_id = ? AND status = 'BOOKED' " +
                        "AND check_in_date < ? AND check_out_date > ?");
        java.util.List<Object> params = new java.util.ArrayList<>();
        params.add(roomTypeId);
        params.add(checkOut);
        params.add(checkIn);
        
        if (excludeReservationId != null) {
            lockSql.append(" AND id != ?");
            params.add(excludeReservationId);
        }
        
        lockSql.append(" FOR UPDATE");
        
        List<Integer> lockedIds = jdbcTemplate.query(lockSql.toString(), (rs, rowNum) -> rs.getInt("id"), 
            params.toArray());
        return lockedIds.size();
    }

    public int countAvailableRooms(Integer roomTypeId) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE room_type_id = ? AND status = 'VACANT'";
        return jdbcTemplate.queryForObject(sql, Integer.class, roomTypeId);
    }
}

