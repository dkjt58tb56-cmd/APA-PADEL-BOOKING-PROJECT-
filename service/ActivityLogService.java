package com.paddlecourt.booking.service;

import com.paddlecourt.booking.model.ActivityLog;
import com.paddlecourt.booking.model.User;
import com.paddlecourt.booking.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Async
    public void log(User user, String action, String description, String ipAddress) {
        try {
            ActivityLog logEntry = ActivityLog.builder()
                    .user(user)
                    .action(action)
                    .description(description)
                    .ipAddress(ipAddress)
                    .build();
            activityLogRepository.save(logEntry);
            log.debug("Logged activity: {} for user {}", action, user != null ? user.getUsername() : "anonymous");
        } catch (Exception e) {
            log.error("Failed to log activity", e);
        }
    }

    public List<ActivityLog> getUserHistory(User user) {
        return activityLogRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<ActivityLog> getRecentLogs() {
        return activityLogRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
