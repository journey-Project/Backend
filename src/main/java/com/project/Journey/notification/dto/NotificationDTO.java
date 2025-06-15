package com.project.Journey.notification.dto;

import com.project.Journey.notification.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor //모든 필드를 초기화할 수 있는 생성자를 추가
@Getter
public class NotificationDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String senderNickname;
    private String message;
    private String link;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

    public static NotificationDTO from(Notification n){
        return NotificationDTO.builder()
                .id(n.getId())
                .senderId(n.getSender() != null ? n.getSender().getId() : null)
                .receiverId(n.getReceiver().getId())
                .senderNickname(n.getReceiver() != null ? n.getSender().getDisplayName() : "SYSTEM")
                .message(n.getMessage())
                .link(n.getLink())
                .type(n.getType().name())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();

    }
}
