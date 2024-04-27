package com.sepehr.telbot.service;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private final ApplicationConfiguration applicationConfiguration;

    private static String MESSAGES_BUCKET = "messagesBucket";

    public void pushMessage(final String message) {
        UUID uuid = UUID.randomUUID();
        final String key = MESSAGES_BUCKET + uuid.toString();
        redisTemplate.opsForValue().append(key, message);
        redisTemplate.expireAt(key, Instant.now().plusSeconds(applicationConfiguration.getMessagesTtl()));
    }

    public List<String> getMessages() {
        Set<String> keys = redisTemplate.keys(MESSAGES_BUCKET + "*");
        if (keys == null)
            keys = new HashSet<>();
        List<String> texts = new ArrayList<>();
        for (String key: keys) {
            texts.add(redisTemplate.opsForValue().get(key));
        }
        return texts;
    }

}
