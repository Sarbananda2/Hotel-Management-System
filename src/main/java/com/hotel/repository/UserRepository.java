package com.hotel.repository;

import com.hotel.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setRole(rs.getString("role"));
            user.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return user;
        }
    };

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, email);
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findById(Integer id) {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, id);
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public User create(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            user.setId(rs.getInt("id"));
            user.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return user;
        }, user.getName(), user.getEmail(), user.getPasswordHash(), user.getRole());
    }

    public List<User> findAll() {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users ORDER BY name";
        return jdbcTemplate.query(sql, USER_ROW_MAPPER);
    }
}

