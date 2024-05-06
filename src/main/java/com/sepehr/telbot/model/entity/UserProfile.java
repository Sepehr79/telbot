package com.sepehr.telbot.model.entity;

import com.sepehr.telbot.model.GptReq;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@RedisHash(value = "UserProfile", timeToLive = 60 * 10)
@Data
@Builder
public class UserProfile {

    @Id
    private String id;

    private String txId;

    private GptReq gptReq;

    private long lastCall;

}
