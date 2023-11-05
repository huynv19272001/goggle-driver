package com.fm.base.models.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@Table(name = "action_history")
public class ActionHistory extends BaseModel {
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "time_start", columnDefinition = "timestamp")
    private LocalDateTime timeStart;

    @Column(name = "time_end", columnDefinition = "timestamp")
    private LocalDateTime timeEnd;

    @Enumerated(EnumType.STRING)
    private OrderAction.Type type;

}
