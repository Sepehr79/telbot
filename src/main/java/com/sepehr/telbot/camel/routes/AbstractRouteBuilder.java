package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.EditMessageTextMessage;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.apache.camel.component.telegram.model.OutgoingMessage;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;

public abstract class AbstractRouteBuilder extends RouteBuilder {

    public OutgoingMessage getOutGoingMessageBuilder(final Exchange exchange, final String text, final InlineKeyboardMarkup replyMarkup) {
        final Integer messageId = exchange.getMessage().getHeader(ApplicationConfiguration.MESSAGE_ID, Integer.class);
        if (messageId == 0) {
            return OutgoingTextMessage.builder()
                    .text(text)
                    .replyMarkup(replyMarkup)
                    .build();
        }
        return EditMessageTextMessage.builder()
                .text(text)
                .replyMarkup(replyMarkup)
                .messageId(messageId)
                .build();
    }

}
