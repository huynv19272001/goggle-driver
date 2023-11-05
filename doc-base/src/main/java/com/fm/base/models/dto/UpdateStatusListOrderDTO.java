package com.fm.base.models.dto;

import com.fm.base.models.sql.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fm.base.message.ErrorMessage.ORDER_IDS_NOT_NULL;
import static com.fm.base.message.ErrorMessage.STATUS_NOT_NULL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class UpdateStatusListOrderDTO {
    @NotNull(message = ORDER_IDS_NOT_NULL)
    private List<Integer> orderIds;

    @NotNull(message = STATUS_NOT_NULL)
    private Order.Status status;
}
