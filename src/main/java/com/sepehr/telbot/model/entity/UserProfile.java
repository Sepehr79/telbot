package com.sepehr.telbot.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(value = "UserProfile", timeToLive = 60 * 10)
@Data
@Builder
public class UserProfile {

    @Id
    private String id;

    private UserState userState;

    private String txId;

    private List<GptMessage> gptMessages;

}
