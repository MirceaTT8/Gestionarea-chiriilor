package com.immobile.real_estate_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immobile.real_estate_backend.model.converter.MaintenanceRequestConverter;
import com.immobile.real_estate_backend.model.dto.MaintenanceRequestDTO;
import com.immobile.real_estate_backend.model.entity.*;
import com.immobile.real_estate_backend.model.enums.MaintenanceStatus;
import com.immobile.real_estate_backend.model.enums.NotificationStatus;
import com.immobile.real_estate_backend.model.enums.NotificationType;
import com.immobile.real_estate_backend.repository.LeaseRepository;
import com.immobile.real_estate_backend.repository.MaintenanceRequestRepository;
import com.immobile.real_estate_backend.repository.ActivityLogRepository;
import com.immobile.real_estate_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MaintenanceRequestService {
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final LeaseRepository leaseRepository;
    private final MaintenanceRequestConverter maintenanceRequestConverter;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ActivityLogRepository activityLogRepository;
    private final ObjectMapper objectMapper;

    public List<MaintenanceRequestDTO> getAllRequests() {
        return maintenanceRequestRepository.findAll()
                .stream()
                .map(maintenanceRequestConverter::toMaintenanceRequestDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public MaintenanceRequestDTO createRequest(Long leaseId, MaintenanceRequestDTO requestDTO, List<MultipartFile> images) throws IOException {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        MaintenanceRequest request = MaintenanceRequest.builder()
                .lease(lease)
                .description(requestDTO.getDescription())
                .status(MaintenanceStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    Image image = Image.builder()
                            .name(file.getOriginalFilename())
                            .content(file.getBytes())
                            .contentType(file.getContentType())
                            .maintenanceRequest(request)
                            .build();
                    request.addImage(image);
                }
            }
        }

        try {
            User landlord = lease.getProperty().getOwner();
            String notificationMessage = String.format("New maintenance request for property %s: %s",
                    lease.getProperty().getAddress(),
                    requestDTO.getDescription().length() > 50
                            ? requestDTO.getDescription().substring(0, 50) + "..."
                            : requestDTO.getDescription());

            Notification notification = Notification.builder()
                    .user(landlord)
                    .title("New Maintenance Request")
                    .message(notificationMessage)
                    .type(NotificationType.MAINTENANCE)
                    .status(NotificationStatus.UNREAD)
                    .build();

            notificationService.createNotification(notification);

        } catch (Exception e) {
            log.error("Failed to create notification for maintenance request", e);
        }

        MaintenanceRequest savedRequest = maintenanceRequestRepository.save(request);

        try {
            ActivityLog logEntry = ActivityLog.builder()
                    .user(lease.getTenant())
                    .actionType("CREATE_MAINTENANCE_REQUEST")
                    .entityType("MaintenanceRequest")
                    .entityId(savedRequest.getRequestId())
                    .details(objectMapper.writeValueAsString(
                            maintenanceRequestConverter.toMaintenanceRequestDTO(savedRequest)))
                    .build();

            activityLogRepository.save(logEntry);
            log.info("Maintenance request created for property {}", lease.getProperty().getAddress());

        } catch (JsonProcessingException e) {
            log.error("Failed to log maintenance request creation: {}", e.getMessage());
        }

        return maintenanceRequestConverter.toMaintenanceRequestDTO(savedRequest);
    }

    public List<MaintenanceRequestDTO> getRequestsByOwnerEmail(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<MaintenanceRequest> requests = maintenanceRequestRepository
                .findAllByLeasePropertyOwnerUserId(owner.getUserId());

        return requests.stream()
                .map(maintenanceRequestConverter::toMaintenanceRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaintenanceRequestDTO markAsNotFixed(Long requestId) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        if (request.getStatus() != MaintenanceStatus.COMPLETED) {
            throw new IllegalStateException("Can only mark completed maintenance requests as not fixed.");
        }

        request.markAsNotFixed();

        MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(request);

        try {
            ActivityLog logEntry = ActivityLog.builder()
                    .user(request.getLease().getTenant())
                    .actionType("MARK_MAINTENANCE_NOT_FIXED")
                    .entityType("MaintenanceRequest")
                    .entityId(updatedRequest.getRequestId())
                    .details(String.format("{\"requestId\":%d,\"description\":\"%s\"}",
                            requestId, request.getDescription()))
                    .build();

            activityLogRepository.save(logEntry);
            log.info("Maintenance request {} marked as not fixed by tenant", requestId);

        } catch (Exception e) {
            log.error("Failed to log maintenance not fixed update: {}", e.getMessage());
        }

        try {
            Notification notification = Notification.builder()
                    .user(request.getLease().getProperty().getOwner())
                    .title("Maintenance Not Fixed")
                    .message(String.format("Tenant reported that maintenance request for '%s' is not actually fixed",
                            request.getDescription().length() > 50
                                    ? request.getDescription().substring(0, 50) + "..."
                                    : request.getDescription()))
                    .type(NotificationType.MAINTENANCE)
                    .status(NotificationStatus.UNREAD)
                    .build();

            notificationService.createNotification(notification);
        } catch (Exception e) {
            log.error("Failed to create not fixed notification", e);
        }

        return maintenanceRequestConverter.toMaintenanceRequestDTO(updatedRequest);
    }

    @Transactional
    public MaintenanceRequestDTO setStatus(Long requestId, MaintenanceStatus status) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        MaintenanceStatus oldStatus = request.getStatus();
        request.setStatus(status);

        if (status == MaintenanceStatus.COMPLETED) {
            request.setIsFixed(true);
        }

        request.setUpdatedAt(LocalDateTime.now());

        MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(request);

        try {
            ActivityLog logEntry = ActivityLog.builder()
                    .user(request.getLease().getProperty().getOwner())
                    .actionType("UPDATE_MAINTENANCE_STATUS")
                    .entityType("MaintenanceRequest")
                    .entityId(updatedRequest.getRequestId())
                    .details(String.format("{\"oldStatus\":\"%s\",\"newStatus\":\"%s\",\"isFixed\":%b}",
                            oldStatus, status, request.getIsFixed()))
                    .build();

            activityLogRepository.save(logEntry);

        } catch (Exception e) {
            log.error("Failed to log maintenance status update: {}", e.getMessage());
        }

        try {
            Notification notification = Notification.builder()
                    .user(request.getLease().getTenant())
                    .title("Maintenance Request Update")
                    .message(String.format("Your maintenance request status changed to %s", status))
                    .type(NotificationType.MAINTENANCE)
                    .status(NotificationStatus.UNREAD)
                    .build();

            notificationService.createNotification(notification);
        } catch (Exception e) {
            log.error("Failed to create status change notification", e);
        }

        return maintenanceRequestConverter.toMaintenanceRequestDTO(updatedRequest);
    }
    public List<MaintenanceRequestDTO> getRequestsByLease(Long leaseId) {
        List<MaintenanceRequest> requests = maintenanceRequestRepository.findByLeaseLeaseId(leaseId);
        return requests.stream()
                .map(maintenanceRequestConverter::toMaintenanceRequestDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceRequestDTO> getRequestsByPropertyOwner(Long ownerId) {
        List<MaintenanceRequest> ownerRequests = maintenanceRequestRepository.findByPropertyOwnerId(ownerId);
        return ownerRequests.stream()
                .map(maintenanceRequestConverter::toMaintenanceRequestDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceRequestDTO setCost(Long requestId, BigDecimal cost) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        if (request.getStatus() != MaintenanceStatus.COMPLETED) {
            throw new IllegalStateException("Cost can only be set for completed maintenance requests.");
        }

        request.setCost(cost);
        maintenanceRequestRepository.save(request);
        return maintenanceRequestConverter.toMaintenanceRequestDTO(request);
    }

}