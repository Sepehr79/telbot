package com.sepehr.telbot;

import com.sepehr.telbot.camel.process.CommandProcessor;
import com.sepehr.telbot.camel.process.TelegramMessagePreProcessor;
import com.sepehr.telbot.camel.process.TextMessageProcessor;
import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.entity.UserState;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class BotManager extends RouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    private final TelegramMessagePreProcessor telegramMessagePreProcessor;

    private final CommandProcessor commandProcessor;

    private final TextMessageProcessor textMessageProcessor;

    @Override
    public void configure() {
        from(applicationConfiguration.getTelegramUri())
                .to("log:in?showHeaders=true")
                .process(telegramMessagePreProcessor).id(TelegramMessagePreProcessor.class.getSimpleName())
                .choice()
                .when(exchange -> exchange.getMessage().getBody(String.class).startsWith("/"))
                    .process(commandProcessor)
                .otherwise()
                    .process(textMessageProcessor)
                .end()
                .choice()
                .when(exchange -> exchange.getMessage().getHeaders().containsKey("DeleteMessage"))
                    .to("seda:deleteMessage").end()
                .toD("direct:${header.route}")
                .process(exchange -> {
                    UserProfile userProfile = exchange.getMessage().getHeader("UserProfile", UserProfile.class);
                    exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, userProfile.getId());
                })
                .to("log:telegramFinalResult?showHeaders=true")
                .to(applicationConfiguration.getTelegramUri());

    }

}
