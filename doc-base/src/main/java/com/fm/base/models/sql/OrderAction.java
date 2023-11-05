package com.fm.base.models.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@Table(name = "order_actions")
public class OrderAction extends BaseModel {

    @Column(name = "time_start", columnDefinition = "timestamp")
    private LocalDateTime timeStart;

    @Column(name = "time_end", columnDefinition = "timestamp")
    private LocalDateTime timeEnd;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "order_id")
    private Integer orderId;

    @Enumerated(EnumType.STRING)
    private Type type;

     public enum Type {
        PRINT, REPRINT ,PACKING, DELIVERED;
    }

}
