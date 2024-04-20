package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.EditMessageDelete;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteMessageRouteBuilder extends RouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    @Override
    public void configure() {
        from("seda:deleteMessage")
                .process(exchange -> {
                    final Integer messageId = exchange.getMessage().getHeader(ApplicationConfiguration.MESSAGE_ID, Integer.class);
                    final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                    final EditMessageDelete editMessageDelete = new EditMessageDelete(chatId, messageId);
                    exchange.getMessage().setBody(editMessageDelete);
                }).to(applicationConfiguration.getTelegramUri());
    }
}
