package com.sepehr.telbot.service;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private final ApplicationConfiguration applicationConfiguration;

    private static String MESSAGES_BUCKET = "messagesBucket";

    public void pushMessage(final String message) {
        final String key = MESSAGES_BUCKET + System.currentTimeMillis();
        redisTemplate.opsForValue().append(key, message);
        redisTemplate.expireAt(key, Instant.now().plusSeconds(applicationConfiguration.getMessagesTtl()));
    }

    public List<String> getMessages() {
        Set<String> ids = redisTemplate.keys(MESSAGES_BUCKET + "*");
        if (ids == null)
            ids = new HashSet<>();
        List<Long> timestamps = ids.stream().map(s -> s.replace(MESSAGES_BUCKET, ""))
                .map(Long::parseLong)
                .sorted()
                .collect(Collectors.toList());
        List<String> texts = new ArrayList<>();
        for (int i = ids.size() - 1; i >= 0; i--) {
            texts.add(redisTemplate.opsForValue().get(MESSAGES_BUCKET + timestamps.get(i)));
        }
        return texts;
    }

}
