package com.fm.base.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class CreateOrderDTO {
    private Integer priceOrder;
    private Integer projectId;
    private Integer priceListId;
    private String orderCode;
    private String note;
}
