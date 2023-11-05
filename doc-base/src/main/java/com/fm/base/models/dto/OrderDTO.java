package com.fm.base.models.dto;

import com.fm.base.models.sql.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class OrderDTO {

    private Order order;

    private List<Order> orderList;

    private Map<Integer,String> filesAttack;
}
