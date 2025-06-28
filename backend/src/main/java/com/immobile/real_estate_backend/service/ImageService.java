package com.immobile.real_estate_backend.service;

import com.immobile.real_estate_backend.model.entity.Image;
import com.immobile.real_estate_backend.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Long uploadImage(MultipartFile multipartImage) {
        try {
            String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartImage.getOriginalFilename()));
            Image image = new Image();
            image.setName(filename);
            image.setContent(multipartImage.getBytes());

            return imageRepository.save(image).getId();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] downloadImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        byte[] content = image.getContent();
        if (content == null || content.length == 0) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Image content is empty");
        }

        return content;
    }
}
