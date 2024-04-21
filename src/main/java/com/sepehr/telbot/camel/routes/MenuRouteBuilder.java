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
                                    .addRow(List.of(InlineKeyboardButton.builder().text("حمایت از ما").callbackData("/donate").build()))
                                    .build();
                    final OutgoingMessage outgoingMessage = getOutGoingTextMessageBuilder(exchange, "سلام! این ربات بهت کمک میکنه که از طریق تلگرام با ChatGPT صحبت کنی.\n\n" +
                            "در حال حاضر برای پاسخ به شما از مدل gpt-3.5-turbo استفاده میشه که میتونین مستقیم از طریق:\n https://chat.openai.com\n بهش دسترسی پیدا کنید.\n\n" +
                            "قابلیت ها:\n1. سوالات خودتون رو بپرسید تا ربات اونها را به ChatGPT ارسال کنه و پاسخش رو به شما برگردونه.\n2. همچنین میتونین این ربات رو به گروه ها اضافه کنید در این صورت برای اینکه ربات رو صدا کنید کافیه اول پیامتون // قرار بدید.\n\n تلاش میکنیم در آینده امکانات بیشتری به این ربات اضافه کنیم.", replyKeyboardMarkup);

                    exchange.getMessage().setBody(outgoingMessage);
                });
    }
}
