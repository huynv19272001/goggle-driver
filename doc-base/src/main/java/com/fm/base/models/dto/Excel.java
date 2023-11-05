package com.fm.base.models.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Optional;

@Data
@AllArgsConstructor
public class Excel {

    private String projectName;

    private String orderCode;

    private String status;

    private DateTime createdAt;

    private DateTime transferTime;

    private Double price;

    private Integer orderId;

    private String note;

    public String getTransferTime() {
        return Optional.ofNullable(transferTime).map(d -> d.toString("dd/MM/yyyy")).orElse("");
    }

    public String getCreatedAt() {
        return Optional.ofNullable(createdAt).map(d -> d.toString("HH:mm | dd/MM/yyyy")).orElse("");
    }
}
