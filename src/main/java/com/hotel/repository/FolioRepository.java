package com.hotel.repository;

import com.hotel.model.Folio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public class FolioRepository {
    private final JdbcTemplate jdbcTemplate;

    public FolioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Folio> FOLIO_ROW_MAPPER = new RowMapper<Folio>() {
        @Override
        public Folio mapRow(ResultSet rs, int rowNum) throws SQLException {
            Folio folio = new Folio();
            folio.setId(rs.getInt("id"));
            Integer stayId = rs.getObject("stay_id", Integer.class);
            folio.setStayId(stayId);
            Integer reservationId = rs.getObject("reservation_id", Integer.class);
            folio.setReservationId(reservationId);
            folio.setCurrency(rs.getString("currency"));
            folio.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return folio;
        }
    };

    public Folio create(Folio folio) {
        String sql = "INSERT INTO folios (stay_id, reservation_id, currency) VALUES (?, ?, ?) RETURNING id, created_at";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            folio.setId(rs.getInt("id"));
            folio.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return folio;
        }, folio.getStayId(), folio.getReservationId(), folio.getCurrency());
    }

    public Optional<Folio> findById(Integer id) {
        String sql = "SELECT id, stay_id, reservation_id, currency, created_at FROM folios WHERE id = ?";
        try {
            Folio folio = jdbcTemplate.queryForObject(sql, FOLIO_ROW_MAPPER, id);
            return Optional.ofNullable(folio);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Folio> findByStayId(Integer stayId) {
        String sql = "SELECT id, stay_id, reservation_id, currency, created_at FROM folios WHERE stay_id = ?";
        try {
            Folio folio = jdbcTemplate.queryForObject(sql, FOLIO_ROW_MAPPER, stayId);
            return Optional.ofNullable(folio);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Folio> findByReservationId(Integer reservationId) {
        String sql = "SELECT id, stay_id, reservation_id, currency, created_at FROM folios WHERE reservation_id = ?";
        try {
            Folio folio = jdbcTemplate.queryForObject(sql, FOLIO_ROW_MAPPER, reservationId);
            return Optional.ofNullable(folio);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

