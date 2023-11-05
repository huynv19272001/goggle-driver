package com.fm.base.repository.sql;

import com.fm.base.models.sql.RefreshToken;
import com.fm.base.models.sql.User;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends BaseRepository<RefreshToken, Integer> {
    @Override
    Optional<RefreshToken> findById(Integer id);

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUserId(Integer userId);
}
