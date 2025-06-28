package com.immobile.real_estate_backend.scheduler;

import com.immobile.real_estate_backend.model.entity.User;
import com.immobile.real_estate_backend.repository.UserRepository;
import com.immobile.real_estate_backend.service.LandlordScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LandlordScoreScheduler {

    private final LandlordScoreService landlordScoreService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 * * * * *")
    public void updateAllLandlordScores() {
        List<User> landlords = userRepository.findAllByRole("LANDLORD");
        landlords.forEach(l -> landlordScoreService.updateScoreForLandlord(l.getUserId()));
    }
}

