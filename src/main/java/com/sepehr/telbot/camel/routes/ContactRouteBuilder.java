package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.InlineKeyboardButton;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContactRouteBuilder extends AbstractRouteBuilder {

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
                .process(exchange -> {
                    final var inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                            .addRow(List.of(InlineKeyboardButton.builder().text("مشاهده منو").callbackData("/start").build()))
                            .addRow(List.of(InlineKeyboardButton.builder().text("چت با ربات").callbackData("/chat").build()))
                            .build();
                    final UserProfile userProfile = exchange.getMessage().getHeader("UserProfile", UserProfile.class);
                    final var outgoingMessage = getOutGoingTextMessageBuilder(exchange, "پیام شما ارسال شد", inlineKeyboardMarkup);
                    exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, userProfile.getId());
                    exchange.getMessage().setBody(outgoingMessage);
                })
                .endChoice().end();
    }
}
