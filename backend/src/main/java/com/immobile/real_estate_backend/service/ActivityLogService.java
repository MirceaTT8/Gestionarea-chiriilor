package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.converter.ActivityLogConverter;
import com.immobile.real_estate_backend.model.dto.ActivityLogDTO;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.repository.ActivityLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogConverter activityConverter;

    public List<ActivityLogDTO> getAllLogs() {
        return activityLogRepository.findAll()
                .stream()
                .map(activityConverter::toDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityLogDTO> getRecentLogsForUser(User user) {
        return activityLogRepository
                .findTop10ByUserUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(activityConverter::toDTO)
                .collect(Collectors.toList());
    }
}
