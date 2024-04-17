package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactRouteBuilder extends RouteBuilder {

    @Value("${telegram.admin.chatId}")
    private String adminChatId;

    private final ApplicationConfiguration applicationConfiguration;

    @Override
    public void configure() {
        from("direct:contact")
                .choice()
                .when(simple("${body} == '/contact'"))
                .transform(constant("بسیارخب لطفا پیام خود را بنویسید.\n نوشته شما به صورت ناشناس به توسعه دهنده ارسال می شود. درصورت تمایل میتوانید مستقیم به @mhmdsphr پیام بدهید"))
                .otherwise()
                .setHeader(TelegramConstants.TELEGRAM_CHAT_ID, constant(adminChatId))
                .to(applicationConfiguration.getTelegramUri())
                .transform(constant("پیام شما ارسال شد.\n /chat  چت با ربات \n /start  مشاهده منو")).endChoice().end();
    }
}
