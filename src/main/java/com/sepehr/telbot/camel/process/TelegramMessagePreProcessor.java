package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.IncomingCallbackQuery;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessagePreProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        int messageId;
        if (exchange.getMessage().getBody() instanceof IncomingCallbackQuery) {
            final IncomingCallbackQuery callbackQuery = exchange.getMessage().getBody(IncomingCallbackQuery.class);
            messageId = Integer.parseInt(String.valueOf(callbackQuery.getMessage().getMessageId()));
            final String chatId = String.valueOf(callbackQuery.getFrom().getId());
            final String callBack = callbackQuery.getData();
            exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
            exchange.getMessage().setHeader(ApplicationConfiguration.BUTTON_RESPONSE, true);
            exchange.getMessage().setBody(callBack);
        } else {
            messageId = exchange.getMessage().getBody(IncomingMessage.class).getMessageId().intValue();
            exchange.getMessage().setHeader(ApplicationConfiguration.BUTTON_RESPONSE, false);
        }
        exchange.getMessage().setHeader(ApplicationConfiguration.MESSAGE_ID, messageId);
        exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_PARSE_MODE, "MARKDOWN");
    }
}
