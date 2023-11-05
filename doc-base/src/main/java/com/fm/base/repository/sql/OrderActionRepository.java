package com.fm.base.repository.sql;

import com.fm.base.models.sql.OrderAction;
import com.fm.base.repository.sql.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderActionRepository extends BaseRepository<OrderAction, Integer> {

    @Query(value = "SELECT * FROM order_actions  WHERE time_end <= now() ", nativeQuery = true)
    List<OrderAction> getAllByTimeEndBeforeTimeNow();

    Optional<OrderAction> findByOrderId(Integer orderId);

    @Query(value = "DELETE  FROM order_actions WHERE id IN (:ids)", nativeQuery = true)
    void deleteByIds(List<Integer> ids);

}
