package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import com.sepehr.telbot.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.IncomingCallbackQuery;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Gathering required data
 */
@Component
@RequiredArgsConstructor
public class TelegramMessagePreProcessor implements Processor {

    private final ActiveChatRepository activeChatRepository;

    private final RedisService redisService;

    @Override
    public void process(Exchange exchange) {
        final var telegramIncomingReq = new AppIncomingReq();
        if (exchange.getMessage().getBody() instanceof IncomingCallbackQuery) { // when glass button is pressed
            final IncomingCallbackQuery callbackQuery = exchange.getMessage().getBody(IncomingCallbackQuery.class);
            Integer messageId = Integer.parseInt(String.valueOf(callbackQuery.getMessage().getMessageId()));
            final String chatId = String.valueOf(callbackQuery.getMessage().getChat().getId());
            String bodyMessage = callbackQuery.getData();
            exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);

            telegramIncomingReq.setMessageId(messageId);
            telegramIncomingReq.setBody(bodyMessage);
            telegramIncomingReq.setIncomingCallbackQuery(callbackQuery);
        } else {
            final IncomingMessage incomingMessage = exchange.getMessage().getBody(IncomingMessage.class);
            String bodyMessage = incomingMessage.getText() != null ? incomingMessage.getText() : incomingMessage.getCaption();
            Integer messageId = exchange.getMessage().getBody(IncomingMessage.class).getMessageId().intValue();
            telegramIncomingReq.setIncomingMessage(incomingMessage);
            telegramIncomingReq.setBody(bodyMessage);
            telegramIncomingReq.setMessageId(messageId);
        }
        exchange.getMessage().setBody(telegramIncomingReq);
        exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_PARSE_MODE, "MARKDOWN");

        redisService.pushMessage(telegramIncomingReq.getBody());
        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
        activeChatRepository.save(new ActiveChat(chatId, System.currentTimeMillis()));
    }
}
