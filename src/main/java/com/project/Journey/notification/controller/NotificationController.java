package com.project.Journey.notification.controller;

import com.project.Journey.notification.dto.NotificationDTO;
import com.project.Journey.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/users/{recipient}/notifications")
    public NotificationDTO handleNotification(@Payload NotificationDTO notificationDTO){
        //notificationService.createNotification(notificationDTO);
        System.out.println("Received notification: " + notificationDTO);
        return notificationService.createNotification(notificationDTO);
    }


    //알림 생성
    @PostMapping("api/notification/createNotification")
    public NotificationDTO createNotification(@RequestBody NotificationDTO notificationDTO){
        return notificationService.createNotification(notificationDTO);
    }


    //수신자(게시글 작성자)가 받은 댓글 알림 리스트 반환
    @GetMapping("api/notification/getNotifications/{recipient}")
    public List<NotificationDTO> getUserNotifications(@PathVariable String recipient){
        return notificationService.getNotificationsForRecipient(recipient);
    }


    //게시글 작성자가 받은 알림을 최신 순으로 정렬
    @GetMapping("api/notification/getNotificationOrderByCreation/{recipient}")
    public List<NotificationDTO> getNotificationsOrderByCreationDateDesc(@PathVariable String recipient){
        return notificationService.getUserNotifications(recipient);
    }


    //댓글 읽음 처리
    @PutMapping("api/notification/markNotification/{notification_id}")
    public NotificationDTO markNotificationAsRead(@PathVariable Long notification_id){
        return notificationService.markAsRead(notification_id);
    }
}
