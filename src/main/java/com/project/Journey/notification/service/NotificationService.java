package com.project.Journey.notification.service;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.notification.dto.NotificationDTO;
import com.project.Journey.notification.entity.Notification;
import com.project.Journey.notification.entity.NotificationType;
import com.project.Journey.notification.repository.NotificationRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EntityManager em;

    @Transactional(readOnly = true)
    public long countUnread(Long receiverId) {
        Member proxy = em.getReference(Member.class, receiverId);
        return notificationRepository.countByReceiverAndReadFalse(proxy);
    }

    public void push(Member receiver,
                     @Nullable Member sender,
                     NotificationType type,
                     String message,
                     String link) {
        if (sender != null && sender.getId().equals(receiver.getId())) {
            return;
        }

        Notification saved = notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .message(message)
                .link(link)
                .build());

        simpMessagingTemplate.convertAndSend("/topic/users/" + receiver.getId() + "/notifications", NotificationDTO.from(saved));
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> list(Long receiverId) {
        Member proxy = em.getReference(Member.class, receiverId);
        return notificationRepository
                .findByReceiverAndIsDeletedFalseOrderByCreatedAtDesc(proxy)
                .stream()
                .map(NotificationDTO::from)
                .toList();
    }

    public void markRead(Long id) {
        notificationRepository.findById(id).ifPresent(Notification::markAsRead);
    }

    public void markAllRead(Long receiverId) {
        Member proxy = em.getReference(Member.class, receiverId);
        List<Notification> notifications = notificationRepository.findByReceiverAndReadFalseAndIsDeletedFalse(proxy);
        notifications.forEach(Notification::markAsRead);
    }

    public void delete(Long id, Long receiverId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다"));

        if (!notification.getReceiver().getId().equals(receiverId)) {
            throw new IllegalStateException("삭제 권한이 없습니다");
        }

        notification.delete();
    }

    // 클라 테스트용
    public void testPush(Member sender, Long receiverId, String type, String message, String link) {
        Member receiver = em.getReference(Member.class, receiverId);

        NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());

        push(receiver, sender, notificationType, message, link);
    }
}
