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
                    final OutgoingMessage outgoingMessage = getOutGoingTextMessageBuilder(exchange, "این ربات بهت کمک میکنه که از طریق تلگرام با ChatGPT صحبت کنی! \n\n " +
                            "آخرین تغییرات: امکان اضافه کردن ربات به گروه ها \n\nدر این صورت برای صدا کردن ربات کافیه اول پیامتون // قرار بدید", replyKeyboardMarkup);

                    exchange.getMessage().setBody(outgoingMessage);
                });
    }
}
