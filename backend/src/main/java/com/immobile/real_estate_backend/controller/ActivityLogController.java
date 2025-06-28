package com.immobile.real_estate_backend.controller;

import com.immobile.real_estate_backend.model.dto.ActivityLogDTO;
import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.service.ActivityLogService;
import com.immobile.real_estate_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/activity-log")
@AllArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final UserService userService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ActivityLogDTO> getAllActivityLogs() {
        return activityLogService.getAllLogs();
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<ActivityLogDTO>> getRecentActivity(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        List<ActivityLogDTO> dtoList = activityLogService.getRecentLogsForUser(user);
        return ResponseEntity.ok(dtoList);
    }
}

