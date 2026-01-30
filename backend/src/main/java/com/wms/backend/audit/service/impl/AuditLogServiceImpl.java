package com.wms.backend.audit.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.audit.entity.AuditLog;
import com.wms.backend.audit.repository.AuditLogRepository;
import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.shared.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String entityType, Long entityId, Object oldValue, Object newValue) {
        try {
            Long userId = null;
            Optional<String> currentUserLogin = SecurityUtil.getCurrentUserLogin();
            if (currentUserLogin.isPresent()) {
                Optional<User> user = userRepository.findByEmail(currentUserLogin.get());
                if (user.isPresent()) {
                    userId = user.get().getId();
                }
            }

            String ipAddress = null;
            String userAgent = null;

            if (RequestContextHolder.getRequestAttributes() != null) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                ipAddress = request.getRemoteAddr();
                userAgent = request.getHeader("User-Agent");
            }

            String oldValueJson = oldValue != null ? objectMapper.writeValueAsString(oldValue) : null;
            String newValueJson = newValue != null ? objectMapper.writeValueAsString(newValue) : null;

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValueJson)
                    .newValue(newValueJson)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }
}
