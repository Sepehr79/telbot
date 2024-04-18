package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.entity.UserState;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TextMessageProcessor implements Processor {

    private final UserProfileRepository userProfileRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);

        final Optional<UserProfile> byId = userProfileRepository.findById(chatId);
        final UserProfile userProfile = byId.orElseGet(() -> UserProfile.builder().id(chatId).userState(UserState.START).build());

        exchange.getMessage().setHeader("UserProfile", userProfile);
        exchange.getMessage().setHeader("route", userProfile.getUserState().toString().toLowerCase());
    }
}
