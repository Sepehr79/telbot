package com.sepehr.telbot.camel.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.EditMessageDelete;
import org.apache.camel.component.telegram.model.IncomingCallbackQuery;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessagePreProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {

        if (exchange.getMessage().getBody() instanceof IncomingCallbackQuery) {
            final IncomingCallbackQuery callbackQuery = exchange.getMessage().getBody(IncomingCallbackQuery.class);
            final Integer messageId = Integer.parseInt(String.valueOf(callbackQuery.getMessage().getMessageId()));
            final String chatId = String.valueOf(callbackQuery.getFrom().getId());
            final String callBack = callbackQuery.getData();
            EditMessageDelete editMessageDelete = new EditMessageDelete(chatId, messageId);
            exchange.getMessage().setHeader("DeleteMessage", editMessageDelete);
            exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
            exchange.getMessage().setBody(callBack);
        }
        exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_PARSE_MODE, "MARKDOWN");
    }
}
