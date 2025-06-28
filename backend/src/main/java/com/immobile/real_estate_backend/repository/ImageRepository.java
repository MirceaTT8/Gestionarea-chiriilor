package com.immobile.real_estate_backend.repository;

import com.immobile.real_estate_backend.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
