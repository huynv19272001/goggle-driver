package com.fm.base.models.sql;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

import javax.persistence.*;

import static com.fm.base.message.ErrorMessage.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Order extends BaseModel {

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "order_code")
    @Length(max = 255, message = LENGTH_PRICE_NAME_NOT_LONGER_THAN_255_CHARACTERS)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer creatorId;

    @Column(name = "transfer_time", columnDefinition = "timestamp")
    private DateTime transferTime;

    private Integer pageTotal;

    private Integer priceListId;

    @Column(name = "receive_time", columnDefinition = "timestamp")
    private DateTime receiveTime;

    @Column(name = "printed_time", columnDefinition = "timestamp")
    private DateTime printedTime;

    @Column(name = "packed_time", columnDefinition = "timestamp")
    private DateTime packedTime;

    @Column(name = "price")
    private Double price;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "note")
    @Length(max = 2000, message = LENGTH_PRICE_NAME_NOT_LONGER_THAN_2000_CHARACTERS)
    private String note;

    @Column(name = "number_reprint")
    private Integer numberReprint;

    public enum Status {
        PENDING, RECEIVED, PRINTED, PACKED, DELIVERED, CANCEL, PRINTING, PACKING
    }

    public void setNote(String note) {
        this.note = note.trim();
    }
}
