package com.fm.base.repository.sql;

import com.fm.base.models.sql.BlacklistToken;

public interface BlacklistTokenRepository extends BaseRepository<BlacklistToken, Integer> {
    BlacklistToken findByAccessToken(String accessToken);
}
