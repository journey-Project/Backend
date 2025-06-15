package com.project.Journey.notification.repository;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByReceiverAndReadFalse(Member receiver);
    List<Notification> findByReceiverAndIsDeletedFalseOrderByCreatedAtDesc(Member receiver);
    List<Notification> findByReceiverAndReadFalseAndIsDeletedFalse(Member receiver);

}
