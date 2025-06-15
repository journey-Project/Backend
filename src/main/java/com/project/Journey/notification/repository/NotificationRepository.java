package com.project.Journey.notification.repository;

import com.project.Journey.login.member.domain.Member;
import com.project.Journey.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(Member receiver);
    long countByReceiverAndReadFalse(Member receiver);
}
