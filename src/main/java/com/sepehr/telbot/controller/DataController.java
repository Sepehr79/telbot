package com.sepehr.telbot.controller;

import com.sepehr.telbot.model.MonitorModel;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import com.sepehr.telbot.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DataController {

    private final ActiveChatRepository activeChatRepository;

    private final RedisService redisService;

    @GetMapping("/data")
    public MonitorModel monitorModel() {
        int activeUsers = activeChatRepository.countAll();
        List<String> messages = redisService.getMessages();
        return new MonitorModel(activeUsers, messages);
    }

}
