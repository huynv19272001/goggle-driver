package com.fm.base.models.sql;

import com.fm.base.models.enums.NotifyType;
import com.fm.base.models.enums.ObjectType;
import lombok.*;

import javax.persistence.*;
import java.util.Set;


@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@With
@Table(name = "notification")
public class Notification extends BaseModel {
    @Column(name="send_user_id")
    private Integer sendUserId;

    @Column(name = "receive_user_id")
    private Integer receiveUserId;

    @Column(name = "object_id")
    private Integer objectId;

    @Enumerated(EnumType.STRING)
    private ObjectType objectType;

    @Enumerated(EnumType.STRING)
    private NotifyType notifyType;

    @Column(name="is_read")
    private Boolean isRead;


}

