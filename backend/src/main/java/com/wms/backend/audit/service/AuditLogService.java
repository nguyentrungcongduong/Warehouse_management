package com.wms.backend.audit.service;

public interface AuditLogService {
    void log(String action, String entityType, Long entityId, Object oldValue, Object newValue);
}
