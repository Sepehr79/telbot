package com.sepehr.telbot;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.entity.UserState;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class BotManager extends RouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    private final UserProfileRepository userProfileRepository;

    @Override
    public void configure() {
        from(applicationConfiguration.getTelegramUri())
                .process(exchange -> {
                    exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_PARSE_MODE, "MARKDOWN");
                    final String body = exchange.getMessage().getBody(String.class);
                    final String chatId = exchange.getMessage().getHeader(ApplicationConfiguration.TELEGRAM_CHAT_ID, String.class);

                    final Optional<UserProfile> byId = userProfileRepository.findById(chatId);
                    final UserProfile userProfile = byId.orElseGet(() -> UserProfile.builder().id(chatId)
                            .userState(UserState.START).build());
                    if (body.matches("/(start|contact|chat)")) {
                        userProfile.setUserState(UserState.valueOf(body.substring(1).toUpperCase()));
                    }
                    userProfileRepository.save(userProfile);
                    exchange.getMessage().setHeader("UserProfile", userProfile);
                    exchange.getMessage().setHeader("route", userProfile.getUserState().toString().toLowerCase());
                })
                .toD("direct:${header.route}")
                .process(exchange -> {
                    UserProfile userProfile = exchange.getMessage().getHeader("UserProfile", UserProfile.class);
                    exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, userProfile.getId());
                })
                .to("log:telegramFinalResult?showHeaders=true")
                .to(applicationConfiguration.getTelegramUri());

    }

}
