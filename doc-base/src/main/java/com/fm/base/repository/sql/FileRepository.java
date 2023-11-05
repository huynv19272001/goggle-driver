package com.fm.base.repository.sql;

import com.fm.base.models.sql.FileAttachment;
import com.fm.base.models.sql.Order;
import com.fm.base.models.sql.PriceList;
import com.fm.base.repository.sql.custom.FileCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends BaseRepository<FileAttachment,Integer>, FileCustomRepository {


    @Transactional
    @Modifying
    @Query("delete from FileAttachment f where f.id=:ids  ")
    int deleteFile(@Param("ids") List<Integer> ids);

    @Query(value = "select file.* from users join user_file on users.id = user_file.user_id join file " +
            "on file.id = user_file.file_id  where users.id = :userLogin order by created_at DESC",
            nativeQuery = true)
    Page<FileAttachment> listFiles(Integer userLogin, Pageable pageable);

    @Query(value = "select * from file_attachment where id in :ids",nativeQuery = true)
    List<FileAttachment> findByListId(List<Integer> ids);

    List<FileAttachment> findByOrderId(int orderId);

    @Query(value = "select * from  file f where f.order_id =:orderId" ,nativeQuery = true)
    List<FileAttachment> FilesByOrderId(Integer orderId);

    @Query(value = "SELECT  *  FROM file_attachment f WHERE f.id=:id AND f.order_id =:orderId  AND deleted_at is null ", nativeQuery = true)
    Optional<FileAttachment> findByIdAndDeletedAtNull(List<Integer> id, Integer orderId);

}
