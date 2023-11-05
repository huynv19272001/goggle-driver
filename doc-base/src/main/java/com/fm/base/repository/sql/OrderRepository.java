package com.fm.base.repository.sql;

import com.fm.base.models.dto.NumberOrderOfProject;
import com.fm.base.models.dto.SummaryAndCount;
import com.fm.base.models.sql.Order;
import com.fm.base.repository.sql.custom.OrderCustomRepository;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends BaseRepository<Order, Integer>, OrderCustomRepository {

    @Query(value = "select * from orders where id in :ids", nativeQuery = true)
    List<Order> findByListId(List<Integer> ids);

    @Query(value = "SELECT new com.fm.base.models.dto.NumberOrderOfProject(o.projectId, count(o)) FROM Order as o WHERE o.deletedAt is null GROUP BY o.projectId")
    List<NumberOrderOfProject> groupByProjectId();

    @Query(value = "SELECT * FROM orders WHERE id=:id AND deleted_at is null", nativeQuery = true)
    Optional<Order> findByIdAndDeletedAtNull(Integer id);

    @Transactional
    @Query(value = "UPDATE orders SET status = :statusReplace WHERE status =:statusOriginal and id IN (:ids)", nativeQuery = true)
    void updateStatusByIds(List<Integer> ids, String statusOriginal, String statusReplace);

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET number_reprint = number_reprint + 1 WHERE id = :id", nativeQuery = true)
    void updateNumberReprint(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET status = :status WHERE id = :id", nativeQuery = true)
    void updateStatusById(Integer id, String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET printed_time = :printedTime WHERE id = :id", nativeQuery = true)
    void updatePrintedTimeById(Integer id, LocalDateTime printedTime);

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET packed_time = :printedTime WHERE id = :id", nativeQuery = true)
    void updatePackedTimeById(Integer id, LocalDateTime printedTime);

    @Query(value = "select * from orders o where o.creator_id =:userId", nativeQuery = true)
    List<Order> listOrderByUser(Integer userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET status = :status, transfer_time = now() WHERE id = :id", nativeQuery = true)
    void updateStatusByIdAndTransferTime(Integer id, String status);

    @Modifying
    @Transactional
    @Query(value = "delete from orders where id =:orderId and creator_id =:creatorId", nativeQuery = true)
    void deleteOrder(Integer orderId, Integer creatorId);

    @Query(value = "select sum(o.price) from orders o where o.status ='DELIVERED'", nativeQuery = true)
    double totalPrice();

    @Transactional
    @Query(value = "select count(o) from orders o where o.status ='DELIVERED'", nativeQuery = true)
    int totalOrder();

    @Query("select new com.fm.base.models.dto.SummaryAndCount(sum(o.price),count(o)) from Order o where o.status ='DELIVERED'")
    SummaryAndCount totalOrderAndRevenue();

    @Query(value = "select  count(u) from users u where u.role = 'USER'", nativeQuery = true)
    int totalPartner();

//    @Query(value = "select new com.fm.base.models.dto.ReportByDay(o.transferTime , sum(o.price), count(o)) from Order o where o.transferTime between :fromDate and :toDate" +
//            " and o.status ='DELIVERED' group by o.transferTime order by o.transferTime")
//    List<ReportByDay> sumAndCountDayByDay(DateTime fromDate, DateTime toDate);

    @Query(value = "select * from orders o where project_id in :projectIds group by o.transfer_time", nativeQuery = true)
    List<Order> listOrderInProject(List<Integer> projectIds);

//    @Query(value = "select new com.fm.base.models.dto.ReportByDay(o.transferTime ,sum(o.price),  count(o)) from Order o " +
//            "where o.status ='DELIVERED' and o.projectId in :projectIds" +
//            " group by o.transferTime")
//    List<ReportByDay> reportByUser(List<Integer> projectIds);

    @Query(value = "select o from Order o where o.projectId=:projectId and o.createdAt between :fromDate and :toDate ")
    List<Order> excelExportListOrder(Integer projectId, DateTime fromDate, DateTime toDate);

    @Query(value = "select * from orders where orders.creator_id =:creatorId order by created_at DESC",
            nativeQuery = true)
    Page<Order> listOrderByUserLogin(Integer creatorId, Pageable pageable);

    Optional<Order> findByOrderCode(String orderCode);
}
