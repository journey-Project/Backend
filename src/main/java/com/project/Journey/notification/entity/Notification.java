package com.project.Journey.notification.entity;

import com.project.Journey.notification.dto.NotificationDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notification_id;

    //@ManyToOne
    @Column(name = "user_id", nullable = false)
    private String user_id; //발신자

    //@ManyToOne
    @Column(name = "recipient", nullable = false)
    private String recipient; // 수신자

    //@JoinColumn
    @Column(name = "post_id", nullable = false)
    private Long post_id;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean is_read;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;



}
