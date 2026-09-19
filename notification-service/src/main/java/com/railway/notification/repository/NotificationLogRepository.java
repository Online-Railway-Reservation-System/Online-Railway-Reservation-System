package com.railway.notification.repository;

import com.railway.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByPnrOrderBySentAtDesc(String pnr);
    List<NotificationLog> findByCustomerIdOrderBySentAtDesc(Long customerId);
    List<NotificationLog> findByChannelOrderBySentAtDesc(String channel);
    List<NotificationLog> findByNotificationTypeOrderBySentAtDesc(String notificationType);
}
