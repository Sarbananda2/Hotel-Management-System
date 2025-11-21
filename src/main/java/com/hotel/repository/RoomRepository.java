package com.hotel.repository;

import com.hotel.model.Room;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepository {
    private final JdbcTemplate jdbcTemplate;

    public RoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Room> ROOM_ROW_MAPPER = new RowMapper<Room>() {
        @Override
        public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
            Room room = new Room();
            room.setId(rs.getInt("id"));
            room.setRoomNumber(rs.getString("room_number"));
            room.setRoomTypeId(rs.getInt("room_type_id"));
            room.setStatus(rs.getString("status"));
            Integer reservationId = rs.getObject("current_reservation_id", Integer.class);
            room.setCurrentReservationId(reservationId);
            return room;
        }
    };

    public List<Room> findAll() {
        String sql = "SELECT id, room_number, room_type_id, status, current_reservation_id FROM rooms ORDER BY room_number";
        return jdbcTemplate.query(sql, ROOM_ROW_MAPPER);
    }

    public Optional<Room> findById(Integer id) {
        String sql = "SELECT id, room_number, room_type_id, status, current_reservation_id FROM rooms WHERE id = ?";
        try {
            Room room = jdbcTemplate.queryForObject(sql, ROOM_ROW_MAPPER, id);
            return Optional.ofNullable(room);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Room> findByStatus(String status) {
        String sql = "SELECT id, room_number, room_type_id, status, current_reservation_id FROM rooms WHERE status = ? ORDER BY room_number";
        return jdbcTemplate.query(sql, ROOM_ROW_MAPPER, status);
    }

    public List<Room> findByRoomTypeIdAndStatus(Integer roomTypeId, String status) {
        String sql = "SELECT id, room_number, room_type_id, status, current_reservation_id FROM rooms WHERE room_type_id = ? AND status = ? ORDER BY room_number";
        return jdbcTemplate.query(sql, ROOM_ROW_MAPPER, roomTypeId, status);
    }

    public void updateStatus(Integer id, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public void updateStatusAndReservation(Integer id, String status, Integer reservationId) {
        String sql = "UPDATE rooms SET status = ?, current_reservation_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, reservationId, id);
    }

    public Room create(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type_id, status) VALUES (?, ?, ?) RETURNING id";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, 
            room.getRoomNumber(), room.getRoomTypeId(), room.getStatus());
        room.setId(id);
        return room;
    }
}

