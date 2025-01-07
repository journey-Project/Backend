package com.project.Journey.notification.service;

import com.project.Journey.notification.dto.NotificationDTO;
import com.project.Journey.notification.entity.Notification;
import com.project.Journey.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;


    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    //알림 생성
    public NotificationDTO createNotification(NotificationDTO notificationDTO){
        Notification notification = new Notification();
        notification.setUser_id(notificationDTO.getUser_id());
        notification.setRecipient(notificationDTO.getRecipient());
        notification.setPost_id(notificationDTO.getPost_id());
        notification.setMessage(notificationDTO.getMessage());
        notification.setCreated_at(LocalDateTime.now());
        notification.set_read(false);
        // DB에 저장
        Notification savedNotification = notificationRepository.save(notification);

        // 알림 전송
        try {
            simpMessagingTemplate.convertAndSend(
                    "/topic/users/" + notificationDTO.getRecipient() + "/notifications",
                    NotificationDTO.fromEntity(savedNotification)
            );
            System.out.println("게시글 작성자 :"+notificationDTO.getRecipient()+"에게 알림을 성공적으로 보냈습니다!");
        } catch (Exception e) {
            System.err.println("알림 보내기에 실패하였습니다. " + e.getMessage());
        }

        return NotificationDTO.fromEntity(savedNotification);
    }


    public void sendNotificationToRecipient(NotificationDTO notificationDTO, Notification savedNotification){
        simpMessagingTemplate.convertAndSend("/topic/users/"+notificationDTO.getRecipient() + "/notifications",
                NotificationDTO.fromEntity(savedNotification));
        System.out.println("게시글 작성자 :"+notificationDTO.getRecipient()+"에게 알림을 성공적으로 보냈습니다!");
    }




    //수신자(게시글 작성자)가 받은 댓글 알림 리스트 반환
    public List<NotificationDTO> getNotificationsForRecipient(String recipient){
        List<Notification> notifications = notificationRepository.findByRecipient(recipient);
        List<NotificationDTO> list = new ArrayList<>();
        for(Notification notification : notifications) {
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .notification_id(notification.getNotification_id())
                    .user_id(notification.getUser_id())
                    .recipient(notification.getRecipient())
                    .post_id(notification.getPost_id())
                    .message(notification.getMessage())
                    .is_read(notification.is_read())
                    .created_at(notification.getCreated_at())
                    .build();

            list.add(notificationDTO);
        }
        return list;
    }

    //특정 사용자의 알림 목록을 조회 - 알림 수신자를 기준으로 최신순(creationDateDesc)으로 알림을 반환
    public List<NotificationDTO> getUserNotifications(String recipient) {
        List<Notification> notifications = notificationRepository.findByRecipient(recipient);
        List<NotificationDTO> creation_date_desc_list = new ArrayList<>();
        for(Notification notification : notifications) {
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .notification_id(notification.getNotification_id())
                    .user_id(notification.getUser_id())
                    .recipient(notification.getRecipient())
                    .post_id(notification.getPost_id())
                    .message(notification.getMessage())
                    .is_read(notification.is_read())
                    .created_at(notification.getCreated_at())
                    .build();

            creation_date_desc_list.add(notificationDTO);
        }
        creation_date_desc_list.sort(new Comparator<NotificationDTO>() {
            @Override
            public int compare(NotificationDTO o1, NotificationDTO o2) {
                return o2.getCreated_at().compareTo(o1.getCreated_at());
            }
        });
        return creation_date_desc_list;
    }


    //특정 게시글에 작성된 댓글을 실시간으로 전송 - 게시글에 새로운 댓글이 작성되면 클라이언트에서 댓글 목록이 즉시 갱신
    public void sendCommentToPost(Long post_id, NotificationDTO notificationDTO ){
        simpMessagingTemplate.convertAndSend("/topic/comments/"+post_id, notificationDTO);
    }

    //특정 댓글이 삭제되었음을 실시간으로 알림 - 클라이언트에서 특정 댓글이 삭제되었을 때 해당 댓글이 화면에서 제거
    public void sendDeleteCommentToPost(Long post_id, NotificationDTO notificationDTO){
        simpMessagingTemplate.convertAndSend("/topic/comments/"+post_id+"/delete",notificationDTO);
    }




    //알림 읽음 처리
    public NotificationDTO markAsRead(Long notification_id){
        Notification notification = notificationRepository.findById(notification_id)
                .orElseThrow(()-> new IllegalArgumentException("notification_id가 "+notification_id+"인 알림을 찾을 수 없습니다"));
        //읽음 처리
        notification.set_read(true);
        notification = notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSend("/topic/users/" + notification.getRecipient() + "/notifications", notification);
        return NotificationDTO.fromEntity(notification);
    }

    //읽지 않은 메시지 리스트
    /*
    public List<NotificationDTO> getUnreadNotifications(String recipient){
        List<Notification> unreadNotifications = notificationRepository.findByRecipientAndIsReadFalse(recipient);
        List<NotificationDTO> unreadList = new ArrayList<>();
        for(Notification notification : unreadNotifications) {
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .notification_id(notification.getNotification_id())
                    .user_id(notification.getUser_id())
                    .recipient(notification.getRecipient())
                    .post_id(notification.getPost_id())
                    .message(notification.getMessage())
                    .is_read(notification.is_read())
                    .created_at(notification.getCreated_at())
                    .build();

            unreadList.add(notificationDTO);
        }
        return unreadList;
    }
*/

}
