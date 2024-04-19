package com.sepehr.telbot.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.InlineKeyboardButton;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:start")
                .process(exchange -> {
                    final OutgoingTextMessage textMessage = new OutgoingTextMessage();
                    textMessage.setText("سلام\n این ربات بهت کمک میکنه که مستقیم با ChatGPT صحبت کنی");

                    final InlineKeyboardMarkup replyKeyboardMarkup =
                            InlineKeyboardMarkup.builder()
                                    .addRow(List.of(InlineKeyboardButton.builder().text("صحبت با ربات").callbackData("/chat").build()))
                                    .addRow(List.of(InlineKeyboardButton.builder().text("پیام ناشناس به توسعه دهنده").callbackData("/contact").build()))
                                    .build();

                    textMessage.setReplyMarkup(replyKeyboardMarkup);
                    exchange.getMessage().setBody(textMessage);
                });
    }
}
