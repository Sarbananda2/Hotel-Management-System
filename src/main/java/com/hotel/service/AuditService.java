package com.hotel.service;

import com.hotel.model.AuditLog;
import com.hotel.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getAuditLogs(Integer userId, String action, String entityType, Integer limit) {
        return auditLogRepository.findAll(userId, action, entityType, limit);
    }
}

