package com.hotel.repository;

import com.hotel.model.AuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class AuditLogRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AuditLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    private RowMapper<AuditLog> createAuditLogRowMapper() {
        return new RowMapper<AuditLog>() {
            @Override
            public AuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
                AuditLog log = new AuditLog();
                log.setId(rs.getInt("id"));
                Integer userId = rs.getObject("user_id", Integer.class);
                log.setUserId(userId);
                log.setAction(rs.getString("action"));
                log.setEntityType(rs.getString("entity_type"));
                Integer entityId = rs.getObject("entity_id", Integer.class);
                log.setEntityId(entityId);
                log.setTimestamp(rs.getObject("timestamp", OffsetDateTime.class));
                
                try {
                    String metaJson = rs.getString("meta");
                    if (metaJson != null) {
                        log.setMeta(objectMapper.readTree(metaJson));
                    }
                } catch (Exception e) {
                    // Ignore JSON parsing errors
                }
                
                return log;
            }
        };
    }

    public void create(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, meta) VALUES (?, ?, ?, ?, ?::jsonb)";
        String metaJson = null;
        if (log.getMeta() != null) {
            try {
                metaJson = objectMapper.writeValueAsString(log.getMeta());
            } catch (Exception e) {
                // Ignore JSON serialization errors
            }
        }
        jdbcTemplate.update(sql, log.getUserId(), log.getAction(), log.getEntityType(), log.getEntityId(), metaJson);
    }

    public List<AuditLog> findAll(Integer userId, String action, String entityType, Integer limit) {
        StringBuilder sql = new StringBuilder("SELECT id, user_id, action, entity_type, entity_id, timestamp, meta FROM audit_logs WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        
        if (userId != null) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }
        if (action != null && !action.trim().isEmpty()) {
            sql.append(" AND action = ?");
            params.add(action);
        }
        if (entityType != null && !entityType.trim().isEmpty()) {
            sql.append(" AND entity_type = ?");
            params.add(entityType);
        }
        
        sql.append(" ORDER BY timestamp DESC LIMIT ?");
        int actualLimit = (limit != null && limit > 0) ? limit : 100;
        params.add(actualLimit);
        
        RowMapper<AuditLog> mapper = createAuditLogRowMapper();
        return jdbcTemplate.query(sql.toString(), mapper, params.toArray());
    }
}

