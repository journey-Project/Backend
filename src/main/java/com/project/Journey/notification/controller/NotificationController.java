package com.project.Journey.notification.controller;

import com.project.Journey.notification.dto.NotificationDTO;
import com.project.Journey.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "알림 관리", description = "댓글 알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "실시간 알림 처리", description = "특정 수신자에게 실시간 알림을 처리하는 메서드입니다.")
    @MessageMapping("/sendMessage")
    @SendTo("/topic/users/{recipient}/notifications")
    public NotificationDTO handleNotification(@Payload NotificationDTO notificationDTO){
        //notificationService.createNotification(notificationDTO);
        System.out.println("Received notification: " + notificationDTO);
        return notificationService.createNotification(notificationDTO);
    }


    //알림 생성
    @Operation(summary = "알림 생성", description = "새로운 알림을 생성하여 데이터베이스에 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림이 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "400", description = "입력값이 잘못되었습니다.", content = @Content)
    })
    @PostMapping("api/notification/createNotification")
    public NotificationDTO createNotification(@RequestBody NotificationDTO notificationDTO){
        return notificationService.createNotification(notificationDTO);
    }


    //수신자(게시글 작성자)가 받은 댓글 알림 리스트 반환
    @Operation(summary = "수신자 알림 조회", description = "특정 수신자가 받은 모든 알림을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "해당 수신자를 찾을 수 없습니다.", content = @Content)
    })
    @GetMapping("api/notification/getNotifications/{recipient}")
    public List<NotificationDTO> getUserNotifications(@PathVariable String recipient){
        return notificationService.getNotificationsForRecipient(recipient);
    }


    //게시글 작성자가 받은 알림을 최신 순으로 정렬
    @GetMapping("api/notification/getNotificationOrderByCreation/{recipient}")
    @Operation(summary = "알림 최신순 조회", description = "특정 수신자가 받은 알림을 최신순으로 정렬하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "해당 수신자를 찾을 수 없습니다.", content = @Content)
    })
    public List<NotificationDTO> getNotificationsOrderByCreationDateDesc(@PathVariable String recipient){
        return notificationService.getUserNotifications(recipient);
    }


    //댓글 읽음 처리
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림이 읽음 상태로 변경되었습니다.",
                    content = @Content(schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "404", description = "해당 알림을 찾을 수 없습니다.", content = @Content)
    })
    @PutMapping("api/notification/markNotification/{notification_id}")
    public NotificationDTO markNotificationAsRead(@PathVariable Long notification_id){
        return notificationService.markAsRead(notification_id);
    }
}
