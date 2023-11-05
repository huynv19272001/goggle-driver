package com.fm.api.payload.request;

import com.fm.base.models.sql.OrderAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@With
@AllArgsConstructor
@Data
public class OrderActionRequest {
    Integer orderId;

    OrderAction.Type typeAction;
}
