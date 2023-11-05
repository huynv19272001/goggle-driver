package com.fm.base.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    List<T> findByAttributeContainsText(String attributeName, String text);

    Optional<T> update(ID id, T t);

    Integer updateIgnoreNull(ID id, T t);
}
