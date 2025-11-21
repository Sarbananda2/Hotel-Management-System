package com.hotel.repository;

import com.hotel.model.Stay;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class StayRepository {
    private final JdbcTemplate jdbcTemplate;

    public StayRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Stay> STAY_ROW_MAPPER = new RowMapper<Stay>() {
        @Override
        public Stay mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stay stay = new Stay();
            stay.setId(rs.getInt("id"));
            Integer reservationId = rs.getObject("reservation_id", Integer.class);
            stay.setReservationId(reservationId);
            Integer roomId = rs.getObject("room_id", Integer.class);
            stay.setRoomId(roomId);
            stay.setActualCheckin(rs.getObject("actual_checkin", OffsetDateTime.class));
            stay.setActualCheckout(rs.getObject("actual_checkout", OffsetDateTime.class));
            Integer folioId = rs.getObject("folio_id", Integer.class);
            stay.setFolioId(folioId);
            return stay;
        }
    };

    public Stay create(Stay stay) {
        String sql = "INSERT INTO stays (reservation_id, room_id, actual_checkin, folio_id) " +
                     "VALUES (?, ?, ?, ?) RETURNING id";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, 
            stay.getReservationId(), stay.getRoomId(), stay.getActualCheckin(), stay.getFolioId());
        stay.setId(id);
        return stay;
    }

    public Optional<Stay> findById(Integer id) {
        String sql = "SELECT id, reservation_id, room_id, actual_checkin, actual_checkout, folio_id FROM stays WHERE id = ?";
        try {
            Stay stay = jdbcTemplate.queryForObject(sql, STAY_ROW_MAPPER, id);
            return Optional.ofNullable(stay);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Stay> findByReservationId(Integer reservationId) {
        String sql = "SELECT id, reservation_id, room_id, actual_checkin, actual_checkout, folio_id FROM stays WHERE reservation_id = ?";
        try {
            Stay stay = jdbcTemplate.queryForObject(sql, STAY_ROW_MAPPER, reservationId);
            return Optional.ofNullable(stay);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateCheckout(Integer id, OffsetDateTime checkout) {
        String sql = "UPDATE stays SET actual_checkout = ? WHERE id = ?";
        jdbcTemplate.update(sql, checkout, id);
    }

    public void updateFolioId(Integer id, Integer folioId) {
        String sql = "UPDATE stays SET folio_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, folioId, id);
    }

    public List<Stay> findActiveStays() {
        String sql = "SELECT id, reservation_id, room_id, actual_checkin, actual_checkout, folio_id " +
                     "FROM stays WHERE actual_checkout IS NULL";
        return jdbcTemplate.query(sql, STAY_ROW_MAPPER);
    }
}

