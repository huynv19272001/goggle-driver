package com.fm.api.service;

import com.fm.api.utils.ListResult;
import com.fm.base.models.sql.Notification;
import com.fm.base.repository.sql.NotificationRepository;
import com.fm.base.utils.PageableUtils;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationDAO;
    public ListResult<Notification> getAll(Pageable pageable) {
        return ListResult.from(notificationDAO.listNotification (pageable));
    }

    public Optional<Notification> updateIsRead(Integer id) {
        Integer loginUserId = UserDetail.getAuthorizedUser().getId();
        Optional<Notification> optionalNotification = notificationDAO.findById(id);
        if(optionalNotification.isPresent())
        {
            notificationDAO.updateIsRead(loginUserId,id);
        }
        return optionalNotification;
    }
    public int updateAllIsRead() {
        Integer loginUserId = UserDetail.getAuthorizedUser().getId();
        return notificationDAO.updateAllIsRead(loginUserId);

    }
}
