package com.fm.base.models.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetToken {
    public static final Long DEFAULT_TIME_TO_LIVE = 60L; // in seconds
    private Integer userId; // usually the objectId of reset target
    private String token; // to confirm the reset request, sent via email/sms
//    private Long timeToLive = DEFAULT_TIME_TO_LIVE;
}