package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.entity.UserState;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandProcessor implements Processor {

    private final UserProfileRepository userProfileRepository;

    @Override
    public void process(Exchange exchange) {
        final String body = exchange.getMessage().getBody(String.class);
        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);

        UserState userState;
        try {
            userState = UserState.valueOf(body.substring(1).toUpperCase());
        } catch (IllegalArgumentException illegalArgumentException) {
            userState = UserState.START;
        }
        final UserProfile userProfile = UserProfile.builder()
                .userState(userState)
                .id(chatId)
                .build();

        userProfileRepository.save(userProfile);
        exchange.getMessage().setHeader("UserProfile", userProfile);
        exchange.getMessage().setHeader("route", userProfile.getUserState().toString().toLowerCase());
    }
}
