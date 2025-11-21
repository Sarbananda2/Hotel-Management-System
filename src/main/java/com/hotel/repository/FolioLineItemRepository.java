package com.hotel.repository;

import com.hotel.model.FolioLineItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class FolioLineItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public FolioLineItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<FolioLineItem> LINE_ITEM_ROW_MAPPER = new RowMapper<FolioLineItem>() {
        @Override
        public FolioLineItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            FolioLineItem item = new FolioLineItem();
            item.setId(rs.getInt("id"));
            item.setFolioId(rs.getInt("folio_id"));
            item.setType(rs.getString("type"));
            item.setDescription(rs.getString("description"));
            item.setAmount(rs.getBigDecimal("amount"));
            item.setPostedAt(rs.getObject("posted_at", OffsetDateTime.class));
            return item;
        }
    };

    public FolioLineItem create(FolioLineItem item) {
        String sql = "INSERT INTO folio_line_items (folio_id, type, description, amount) VALUES (?, ?, ?, ?) RETURNING id, posted_at";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            item.setId(rs.getInt("id"));
            item.setPostedAt(rs.getObject("posted_at", OffsetDateTime.class));
            return item;
        }, item.getFolioId(), item.getType(), item.getDescription(), item.getAmount());
    }

    public List<FolioLineItem> findByFolioId(Integer folioId) {
        String sql = "SELECT id, folio_id, type, description, amount, posted_at FROM folio_line_items WHERE folio_id = ? ORDER BY posted_at";
        return jdbcTemplate.query(sql, LINE_ITEM_ROW_MAPPER, folioId);
    }
}

