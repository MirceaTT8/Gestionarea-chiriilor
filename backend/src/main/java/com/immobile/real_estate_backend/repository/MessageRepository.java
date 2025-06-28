package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
