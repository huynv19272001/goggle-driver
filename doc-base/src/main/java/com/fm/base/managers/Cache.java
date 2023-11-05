package com.fm.base.managers;

import com.fm.base.models.cache.ResetToken;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//@EnableCaching
//@Configuration
public class Cache {
    public static LoadingCache<Integer, ResetToken> resetTokenCache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(3,TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public ResetToken load(Integer userId) {
                    return new ResetToken(
                            userId,
//                            UUID.randomUUID().toString().replaceAll("[-]", "")
                            new DecimalFormat("000000").format(new Random().nextInt(999999))
                    );
                }
            });
}

