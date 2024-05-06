package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.entity.Model;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.IncomingCallbackQuery;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.springframework.stereotype.Component;

/**
 * Gathering required data
 */
@Component
@RequiredArgsConstructor
public class TelegramMessagePreProcessor implements Processor {

    private final ActiveChatRepository activeChatRepository;

    private final ApplicationConfiguration applicationConfiguration;

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
            String bodyMessage = incomingMessage.getText() != null ? incomingMessage.getText() : "";
            Integer messageId = exchange.getMessage().getBody(IncomingMessage.class).getMessageId().intValue();
            telegramIncomingReq.setIncomingMessage(incomingMessage);
            telegramIncomingReq.setBody(bodyMessage);
            telegramIncomingReq.setMessageId(messageId);
        }
        exchange.getMessage().setBody(telegramIncomingReq);

        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);

        ActiveChat activeChat = activeChatRepository.findById(chatId)
                .orElse(new ActiveChat(chatId, System.currentTimeMillis(), applicationConfiguration.getDefaultBalance(), Model.GPT35));
        activeChatRepository.save(activeChat);
        exchange.getMessage().setHeader(ApplicationConfiguration.ACTIVE_CHAT, activeChat);
    }
}
