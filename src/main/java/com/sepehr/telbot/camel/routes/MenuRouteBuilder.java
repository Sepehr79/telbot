package com.sepehr.telbot.camel.routes;

import org.apache.camel.component.telegram.model.InlineKeyboardButton;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.apache.camel.component.telegram.model.OutgoingMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuRouteBuilder extends AbstractRouteBuilder {
    @Override
    public void configure() {
        from("direct:start")
                .process(exchange -> {
                    final InlineKeyboardMarkup replyKeyboardMarkup =
                            InlineKeyboardMarkup.builder()
                                    .addRow(List.of(InlineKeyboardButton.builder().text("صحبت با ربات").callbackData("/chat").build()))
                                    .addRow(List.of(InlineKeyboardButton.builder().text("پیام ناشناس به توسعه دهنده").callbackData("/contact").build()))
                                    .build();
                    final OutgoingMessage outgoingMessage = getOutGoingMessageBuilder(exchange, "سلام\n این ربات بهت کمک میکنه که مستقیم با ChatGPT صحبت کنی", replyKeyboardMarkup);

                    exchange.getMessage().setBody(outgoingMessage);
                });
    }
}
