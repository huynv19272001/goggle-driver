package com.fm.base.repository.sql;

import com.fm.base.models.sql.ActionHistory;

public interface ActionHistoryRepository extends BaseRepository<ActionHistory, Integer> {

//    @Query(value = "SELECT * FROM order_actions  WHERE time_end <= now() and deleted_at is null", nativeQuery = true)
//    List<OrderAction> getAllByTimeEndAfterTimeNow();
//
//    @Query(value = "UPDATE order_actions SET deleted_at = now() WHERE id IN (:ids)", nativeQuery = true)
//    void deleteByIds(List<Integer> ids);

}
