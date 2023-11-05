package com.fm.base.repository.sql;

import com.fm.base.models.sql.Project;
import com.fm.base.models.sql.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends BaseRepository<Project, Integer> {

    @Query(value = "SELECT * FROM projects WHERE deleted_at is null", nativeQuery = true)
    List<Project> findAll();

//    @Query(value = "SELECT p FROM Project p WHERE"
//            + "(:name IS NULL OR p.name LIKE %:name% )"
//            + "AND (:code IS NULL OR p.code LIKE %:code% )"
//            + "AND (:key IS NULL OR p.code LIKE %:key% OR p.name LIKE %:key% )"
//            + "AND (p.deletedAt IS NULL)")
    @Query(value = "SELECT * FROM projects WHERE " +
            "                :name IS NULL OR lower(name) LIKE lower(concat('%', concat(:name, '%'))) " +
            "                AND (:code IS NULL OR lower(code) LIKE lower(concat('%', concat(:code, '%'))) ) " +
            "                AND (:key IS NULL OR lower(code) LIKE lower(concat('%', concat(:key, '%'))) OR lower(name) LIKE lower(concat('%', concat(:key, '%')))) " +
            "                AND (deleted_at IS NULL) ORDER BY created_at desc", nativeQuery = true)
    Page<Project> filter(String name, String code, String key, Pageable pageable);

    @Query(value = "SELECT  *  FROM projects p WHERE ( p.name =:name OR p.code =:code) AND deleted_at is null limit 1", nativeQuery = true)
    Optional<Project> findByNameOrCode(String name, String code);

    @Query(value = "SELECT  *  FROM projects p WHERE p.id != :id AND ( p.name =:name OR p.code =:code) AND deleted_at is null", nativeQuery = true)
    Optional<Project> findByIdAndNameOrCode(Integer id, String name, String code);

    @Query(value = "SELECT  *  FROM projects p WHERE p.id=:id AND deleted_at is null", nativeQuery = true)
    Optional<Project> findByIdDeletedAtNull(Integer id);

    @Query(value = "SELECT  *  FROM projects p WHERE p.id=:id AND deleted_at is null", nativeQuery = true)
    Optional<Project> findByIdAndDeletedAtNull(Integer id);

    @Query(value = "SELECT  *  FROM projects p WHERE p.id=:id AND p.user_id=:userLogin AND deleted_at is null", nativeQuery = true)
    Optional<Project> findByProjectIdToUserAndDeletedAtNull(Integer id,Integer userLogin);
    @Query(value = "SELECT  *  FROM projects p WHERE p.id=:id AND deleted_at is null", nativeQuery = true)
    Optional<Project> findByProjectIdToUserAndDeletedAtNullAdmin(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE projects SET deleted_at = now() WHERE id = :id", nativeQuery = true)
    void deleteById(Integer id);

    @Query(value = "select p.id from projects p where user_id =:userId", nativeQuery = true)
    List<Integer> listUserProjectIds(Integer userId);

    @Query(value = "select * from projects where dashboard =:dashboard order by created_at desc  limit 5", nativeQuery = true)
    List<Project> findProjectShow(String dashboard);

    @Query(value = "select * from projects where ?1 is null or user_id = CAST(CAST(?1 AS TEXT) AS INT)", nativeQuery = true)
    List<Project> listProjectId(Integer userId);
}
