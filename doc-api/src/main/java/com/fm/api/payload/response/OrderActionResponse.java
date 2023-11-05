package com.fm.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class OrderActionResponse {

    private Integer orderId;

    private String nameAction;

    private Double timePrint;

    private Double timePacking;

}
