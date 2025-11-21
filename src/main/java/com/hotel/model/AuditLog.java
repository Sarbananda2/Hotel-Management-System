package com.hotel.model;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.databind.JsonNode;

public class AuditLog {
    private Integer id;
    private Integer userId;
    private String action;
    private String entityType;
    private Integer entityId;
    private OffsetDateTime timestamp;
    private JsonNode meta;

    public AuditLog() {}

    public AuditLog(Integer id, Integer userId, String action, String entityType, 
                   Integer entityId, OffsetDateTime timestamp, JsonNode meta) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = timestamp;
        this.meta = meta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public JsonNode getMeta() {
        return meta;
    }

    public void setMeta(JsonNode meta) {
        this.meta = meta;
    }
}

