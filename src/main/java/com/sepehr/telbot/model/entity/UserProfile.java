package com.sepehr.telbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("UserProfile")
@Data
@AllArgsConstructor
public class UserProfile {

    @Id
    private String id;

    private String chatId;

    private String txId;

}
