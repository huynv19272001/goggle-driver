package com.fm.api.service;

import com.fm.base.models.sql.BlacklistToken;
import com.fm.base.repository.sql.BlacklistTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class BlacklistService {
    private final BlacklistTokenRepository blacklistTokenRepository;
    public BlacklistToken save(BlacklistToken blacklistToken){
        return blacklistTokenRepository.save(blacklistToken);
    }
}
