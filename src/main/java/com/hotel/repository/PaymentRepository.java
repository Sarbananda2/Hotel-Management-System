package com.hotel.repository;

import com.hotel.model.Payment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {
    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Payment> PAYMENT_ROW_MAPPER = new RowMapper<Payment>() {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Payment payment = new Payment();
            payment.setId(rs.getInt("id"));
            payment.setFolioId(rs.getInt("folio_id"));
            payment.setAmount(rs.getBigDecimal("amount"));
            payment.setMethod(rs.getString("method"));
            payment.setReference(rs.getString("reference"));
            payment.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return payment;
        }
    };

    public Payment create(Payment payment) {
        String sql = "INSERT INTO payments (folio_id, amount, method, reference) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            payment.setId(rs.getInt("id"));
            payment.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return payment;
        }, payment.getFolioId(), payment.getAmount(), payment.getMethod(), payment.getReference());
    }

    public List<Payment> findByFolioId(Integer folioId) {
        String sql = "SELECT id, folio_id, amount, method, reference, created_at FROM payments WHERE folio_id = ? ORDER BY created_at";
        return jdbcTemplate.query(sql, PAYMENT_ROW_MAPPER, folioId);
    }

    public Optional<Payment> findById(Integer id) {
        String sql = "SELECT id, folio_id, amount, method, reference, created_at FROM payments WHERE id = ?";
        try {
            Payment payment = jdbcTemplate.queryForObject(sql, PAYMENT_ROW_MAPPER, id);
            return Optional.ofNullable(payment);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public BigDecimal sumCashPaymentsByDate(LocalDate date) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments " +
                     "WHERE method = 'CASH' AND DATE(created_at) = ?";
        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class, date);
        return result != null ? result : BigDecimal.ZERO;
    }
}

