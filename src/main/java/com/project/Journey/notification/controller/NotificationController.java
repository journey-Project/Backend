package com.project.Journey.notification.controller;

import com.project.Journey.login.auth.CustomUserDetails;
import com.project.Journey.notification.dto.NotificationDTO;
import com.project.Journey.notification.entity.Notification;
import com.project.Journey.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 관리", description = "댓글 알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping
    public List<NotificationDTO> myList(@AuthenticationPrincipal CustomUserDetails me) {
        return notificationService.list(me.getMember().getId());
    }

    @Operation(summary = "알림 읽음 처리")
    @PatchMapping("/{id}/read")
    public void read(@PathVariable Long id) {
        notificationService.markRead(id);
    }

    @Operation(summary = "읽지 않은 알림 개수 조회")
    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(notificationService.countUnread(user.getMember().getId()));
    }

    @Operation(summary = "전체 알림 읽음 처리")
    @PatchMapping("/read-all")
    public ResponseEntity<Void> readAll(@AuthenticationPrincipal CustomUserDetails user) {
        notificationService.markAllRead(user.getMember().getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 삭제(soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        notificationService.delete(id, user.getMember().getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[테스트] 알림 푸시 전송", description = "알림 전송 테스트용 API입니다.")
    @PostMapping("/test-push")
    public void testPush(
            @AuthenticationPrincipal CustomUserDetails sender,
            @RequestParam Long receiverId,
            @RequestParam String type,
            @RequestParam String message,
            @RequestParam String link
    ) {
        notificationService.testPush(sender.getMember(), receiverId, type, message, link);
    }

}
