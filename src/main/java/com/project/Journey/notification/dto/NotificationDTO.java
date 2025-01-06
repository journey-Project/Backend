package com.project.Journey.notification.dto;

import com.project.Journey.notification.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor //모든 필드를 초기화할 수 있는 생성자를 추가
@Getter
@Setter
public class NotificationDTO {

    private Long notification_id;
    private String user_id; //발신자

    private String recipient; // 수신자

    private Long post_id;

    private String message;

    private boolean is_read;

    private LocalDateTime created_at;

    public NotificationDTO(){

    }

    //Notification 엔티티를 DTO로 변환
    public static NotificationDTO fromEntity(Notification notification){
        return new NotificationDTO(
                notification.getNotification_id(),
                notification.getUser_id(),
                notification.getRecipient(),
                notification.getPost_id(),
                notification.getMessage(),
                notification.is_read(),
                notification.getCreated_at()

        );
    }
}
