package com.immobile.real_estate_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver", nullable = false)
    private User receiver;

    @Column(name = "text", nullable = false, length = 1000)
    private String text;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private LocalDateTime localDateTime;

}
