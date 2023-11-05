package com.fm.base.repository.sql;

import com.fm.base.models.sql.Notification;
import com.fm.base.repository.sql.custom.NotificationCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface NotificationRepository extends BaseRepository<Notification, Integer> , NotificationCustomRepository {
    @Transactional
    @Modifying
    @Query(value = "UPDATE Notification n SET n.isRead =true WHERE n.receiveUserId= :userId and n.id = :id")
    int updateIsRead(Integer userId, Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Notification n SET n.isRead =true WHERE n.receiveUserId=:userId ")
    int updateAllIsRead(Integer userId);

    @Query(value = "SELECT n FROM Notification n  ")
    Page<Notification> listNotification (Pageable pageable);
}
