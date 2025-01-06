package com.project.Journey.notification.repository;

import com.project.Journey.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(String recipient);

    //List<Notification> findByRecipientOrderByCreationDateDesc(String recipient);

    //읽지 않은 메시지 반환
   // List<Notification> findByRecipientAndIsReadFalse(String recipient);
}
