package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.Command;
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
public class TelegramMessagePreProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        int messageId;
        String stringCommand;
        if (exchange.getMessage().getBody() instanceof IncomingCallbackQuery) { // when glass button is pressed
            final IncomingCallbackQuery callbackQuery = exchange.getMessage().getBody(IncomingCallbackQuery.class);
            messageId = Integer.parseInt(String.valueOf(callbackQuery.getMessage().getMessageId()));
            final String chatId = String.valueOf(callbackQuery.getFrom().getId());
            stringCommand = callbackQuery.getData().substring(1);
            exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
            exchange.getMessage().setHeader(ApplicationConfiguration.BUTTON_RESPONSE, true);
        } else {
            stringCommand = exchange.getMessage().getBody(IncomingMessage.class).getText().substring(1);
            messageId = exchange.getMessage().getBody(IncomingMessage.class).getMessageId().intValue();
            exchange.getMessage().setHeader(ApplicationConfiguration.BUTTON_RESPONSE, false);
        }
        Command command;
        try {
            command = Command.valueOf(stringCommand.toUpperCase());
        } catch (IllegalArgumentException illegalArgumentException) {
            command = Command.CHAT;
        }
        exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, command.toString().toLowerCase());
        exchange.getMessage().setHeader(ApplicationConfiguration.REPLY_MESSAGE_ID, messageId);
        exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_PARSE_MODE, "MARKDOWN");
    }
}
