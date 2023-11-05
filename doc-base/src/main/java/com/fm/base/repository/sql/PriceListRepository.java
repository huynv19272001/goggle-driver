package com.fm.base.repository.sql;

import com.fm.base.models.sql.PriceList;
import com.fm.base.models.sql.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PriceListRepository extends BaseRepository<PriceList, Integer> {

    @Query(value = "SELECT  *  FROM price_list ", nativeQuery = true)
    Page<PriceList> listPriceList(Pageable pageable);

    @Query(value = "SELECT  *  FROM price_list p WHERE p.price_name=:namePrice AND p.project_id=:projectId", nativeQuery = true)
    Optional<PriceList> findNamePriceExits(String namePrice, Integer projectId);

    @Query(value = "SELECT  *  FROM price_list p WHERE p.id=:id AND deleted_at is null", nativeQuery = true)
    Optional<PriceList> findByIdDeletedAtNull(Integer id);

    @Query(value = "SELECT  *  FROM price_list p  WHERE p.project_id = :projectId AND now() >= start_time AND now() <= end_time  AND deleted_at IS NULL LIMIT 1 ", nativeQuery = true)
    Optional<PriceList> findByProjectIdAndTimeNow(Integer projectId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE price_list SET deleted_at = now() WHERE id = :id", nativeQuery = true)
    void deleteById(Integer id);

    @Query(value = "SELECT * FROM price_list WHERE deleted_at is null",
            countQuery = "SELECT  count(*)  FROM price_list WHERE deleted_at is null",
            nativeQuery = true)
    List<PriceList> listPriceList();

    @Query(value = "SELECT  *  FROM price_list p WHERE p.criteria_id= :criteriaId ", nativeQuery = true)
    Optional<PriceList> checkCriteriaIdExits(Integer criteriaId);

    @Query(value = "SELECT  p.end_time FROM price_list p WHERE p.partner_id=:partnerId AND deleted_at is null", nativeQuery = true)
    List<Date> checkEndTime(Integer partnerId);

    @Query(value = "Select Min(p.end_time) from  (WITH cte AS (SELECT *, rank() OVER ( ORDER BY p.id desc ) AS rnk FROM   price_list p" +
            " Where p.partner_id=:partnerId) SELECT * FROM   cte WHERE  rnk <= 2) as p", nativeQuery = true)
    Date getDayLatest(Integer partnerId);

    @Query(value = "SELECT p.* FROM price_list p LEFT JOIN projects p2 on p.project_id = p2.id where p2.id=:projectId", nativeQuery = true)
    Page<PriceList> listPriceListByProjectName(Integer projectId,Pageable pageable);

    @Query(value = "SELECT  *  FROM price_list p WHERE p.id=:id AND p.project_id =:projectId  AND deleted_at is null ", nativeQuery = true)
    Optional<PriceList> findByIdAndDeletedAtNull(Integer id,Integer projectId);

    @Query(value = "SELECT p.* FROM price_list p LEFT JOIN projects p2 on p.project_id = p2.id where p2.id=:projectId", nativeQuery = true)
    List<PriceList> listPriceListByProjectId(Integer projectId);
}
