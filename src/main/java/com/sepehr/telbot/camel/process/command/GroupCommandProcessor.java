package com.sepehr.telbot.camel.process.command;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupCommandProcessor implements Processor {

    private final UserProfileRepository userProfileRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getMessage().getBody(String.class);
        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
        if (body.startsWith("//")) {
            body = body.substring(2);

            UserProfile userProfile = userProfileRepository.findById(chatId)
                    .orElse(UserProfile.builder().id(chatId).build());
            exchange.getMessage().setBody(body);
            userProfileRepository.save(userProfile);
            exchange.getMessage().setHeader("UserProfile", userProfile);
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "chat");
        } else {
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "ignore");
        }
    }
}
