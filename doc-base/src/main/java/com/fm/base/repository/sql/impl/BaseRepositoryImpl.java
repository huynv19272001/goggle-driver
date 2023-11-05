package com.fm.base.repository.sql.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fm.base.models.sql.BaseModel;
import com.fm.base.repository.sql.BaseRepository;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.*;

public class BaseRepositoryImpl<T extends BaseModel> extends SimpleJpaRepository<T, Integer> implements BaseRepository<T, Integer> {
    protected final ObjectMapper MAPPER = new ObjectMapper();
    protected final EntityManager entityManager;
    protected final CriteriaBuilder builder;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.builder = entityManager.getCriteriaBuilder();
    }

    @Override
    public List<T> findByAttributeContainsText(String attributeName, String text) {
        CriteriaQuery<T> cQuery = builder.createQuery(getDomainClass());
        Root<T> root = cQuery.from(getDomainClass());
        cQuery.select(root).where(builder.like(root.get(attributeName), text));
        return entityManager.createQuery(cQuery).getResultList();
    }

    @Override
    @Modifying
    @Transactional
    public Optional<T> update(Integer id, T t) {
        return findById(id).map(exist -> {
            t.setId(exist.getId());
            t.setCreatedAt(exist.getCreatedAt());
            t.setUpdatedAt(new DateTime());
            return save(t);
        });
    }

    @Override
    public Integer updateIgnoreNull(Integer id, T t) {
        t.setUpdatedAt(new DateTime());
        Map<String, Object> tAsMap = MAPPER.convertValue(t, Map.class);

        if (tAsMap != null && !tAsMap.isEmpty() && !tAsMap.values().stream().allMatch(Objects::isNull)) {
            CriteriaUpdate<T> cUpdate = builder.createCriteriaUpdate(getDomainClass());
            Root<T> root = cUpdate.from(getDomainClass());
            cUpdate.where(builder.equal(root.get("id"), id));

            tAsMap.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && !entry.getKey().equals("id"))
                    .forEach(entry -> {
                        cUpdate.set(root.get(entry.getKey()), entry.getValue());
                    });
            return entityManager.createQuery(cUpdate).executeUpdate();
        }
        return 0;
    }
}
