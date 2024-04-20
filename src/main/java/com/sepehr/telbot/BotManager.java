package com.sepehr.telbot;

import com.sepehr.telbot.camel.process.TelegramMessagePreProcessor;
import com.sepehr.telbot.camel.process.TextMessageProcessor;
import com.sepehr.telbot.camel.process.command.GroupCommandProcessor;
import com.sepehr.telbot.camel.process.command.UserCommandProcessor;
import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class BotManager extends RouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    private final TelegramMessagePreProcessor telegramMessagePreProcessor;

    private final UserCommandProcessor userCommandProcessor;

    private final GroupCommandProcessor groupCommandProcessor;

    private final TextMessageProcessor textMessageProcessor;

    @Value("${app.version}")
    private String appVersion;

    @Override
    public void configure() {
        from(applicationConfiguration.getTelegramUri())
                .to("log:in?showHeaders=true")
                .process(telegramMessagePreProcessor).id(TelegramMessagePreProcessor.class.getSimpleName())
                .choice()
                .when(exchange -> exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class).startsWith("-"))
                    .process(groupCommandProcessor).id(GroupCommandProcessor.class.getSimpleName())
                .when(exchange -> exchange.getMessage().getBody(String.class).startsWith("/"))
                    .process(userCommandProcessor).id(UserCommandProcessor.class.getSimpleName())
                .otherwise()
                    .process(textMessageProcessor).id(TextMessageProcessor.class.getSimpleName())
                .end()
                .toD("direct:${header.route}", true)
                .to("log:telegramFinalResult?showHeaders=true")
                .to(applicationConfiguration.getTelegramUri());

        from("direct:version")
                .transform(constant(appVersion));

        from("direct:ignore")
                .to("log:ignoreMessage")
                .stop();

    }

}
