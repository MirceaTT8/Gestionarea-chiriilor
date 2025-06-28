package com.immobile.real_estate_backend.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String text;
    private LocalDateTime timestamp;
}