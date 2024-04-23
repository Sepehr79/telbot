package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.IncomingCallbackQuery;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Gathering required data
 */
@Component
@RequiredArgsConstructor
public class TelegramMessagePreProcessor implements Processor {

    private final ActiveChatRepository activeChatRepository;

    @Override
    public void process(Exchange exchange) {
        int messageId;
        String bodyMessage;
        if (exchange.getMessage().getBody() instanceof IncomingCallbackQuery) { // when glass button is pressed
            final IncomingCallbackQuery callbackQuery = exchange.getMessage().getBody(IncomingCallbackQuery.class);
            messageId = Integer.parseInt(String.valueOf(callbackQuery.getMessage().getMessageId()));
            final String chatId = String.valueOf(callbackQuery.getMessage().getChat().getId());
            bodyMessage = callbackQuery.getData();
            exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
            exchange.getMessage().setHeader(ApplicationConfiguration.BUTTON_RESPONSE, true);
        } else {
            final IncomingMessage incomingMessage = exchange.getMessage().getBody(IncomingMessage.class);
            bodyMessage = incomingMessage.getText() != null ? incomingMessage.getText() : incomingMessage.getCaption();
            messageId = exchange.getMessage().getBody(IncomingMessage.class).getMessageId().intValue();
            exchange.getMessage().setHeader(ApplicationConfiguration.BUTTON_RESPONSE, false);
        }
        exchange.getMessage().setHeader(ApplicationConfiguration.BODY_MESSAGE, bodyMessage);
        exchange.getMessage().setHeader(ApplicationConfiguration.REPLY_MESSAGE_ID, messageId);
        exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_PARSE_MODE, "MARKDOWN");

        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
        activeChatRepository.save(new ActiveChat(chatId, System.currentTimeMillis()));
    }
}
