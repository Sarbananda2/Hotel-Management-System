package com.hotel.repository;

import com.hotel.model.RoomType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomTypeRepository {
    private final JdbcTemplate jdbcTemplate;

    public RoomTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<RoomType> ROOM_TYPE_ROW_MAPPER = new RowMapper<RoomType>() {
        @Override
        public RoomType mapRow(ResultSet rs, int rowNum) throws SQLException {
            RoomType roomType = new RoomType();
            roomType.setId(rs.getInt("id"));
            roomType.setName(rs.getString("name"));
            roomType.setCapacity(rs.getInt("capacity"));
            roomType.setBaseRate(rs.getBigDecimal("base_rate"));
            roomType.setDescription(rs.getString("description"));
            return roomType;
        }
    };

    public List<RoomType> findAll() {
        String sql = "SELECT id, name, capacity, base_rate, description FROM room_types ORDER BY id";
        return jdbcTemplate.query(sql, ROOM_TYPE_ROW_MAPPER);
    }

    public Optional<RoomType> findById(Integer id) {
        String sql = "SELECT id, name, capacity, base_rate, description FROM room_types WHERE id = ?";
        try {
            RoomType roomType = jdbcTemplate.queryForObject(sql, ROOM_TYPE_ROW_MAPPER, id);
            return Optional.ofNullable(roomType);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public RoomType create(RoomType roomType) {
        String sql = "INSERT INTO room_types (name, capacity, base_rate, description) VALUES (?, ?, ?, ?) RETURNING id";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, 
            roomType.getName(), roomType.getCapacity(), roomType.getBaseRate(), roomType.getDescription());
        roomType.setId(id);
        return roomType;
    }
}

