package com.sepehr.telbot.schedule;

import com.sepehr.telbot.model.repo.ActiveChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ApplicationSchedule {

    private final ActiveChatRepository activeChatRepository;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 7)
    @Transactional
    public void deleteChatsWhenNotActiveFor1Month() {
        long time = System.currentTimeMillis();
        activeChatRepository.deleteActiveChatByLastUpdateLessThan(
                time - 1000L * 60  * 60 * 24 * 30
        );
    }

}
