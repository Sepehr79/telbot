package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.ActiveChat;
import org.apache.camel.component.telegram.model.InlineKeyboardButton;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BalanceRouteBuilder extends AbstractRouteBuilder {
    protected BalanceRouteBuilder(ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration);
    }

    @Override
    public void configureOtherRoutes() {
        from("direct:balance")
                .process(exchange -> {
                    final String text = "موجودی فعلی شما به میزان %d توکن می باشد. در صورت تمایل میتوانید با خرید اشتراک از GPT-4 استفاده بیشتری بکنید. در غیراینصورت مدل مورد استفاده خود را به GPT-3 تغییر بدهید.";
                    final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
                    final String message = String.format(text, activeChat.getBalance());
                    OutgoingTextMessage outgoingTextMessage = new OutgoingTextMessage();
                    outgoingTextMessage.setParseMode("markdown");
                    outgoingTextMessage.setText(message);
                    InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                            .addRow(List.of(InlineKeyboardButton.builder().text("یکماهه روزانه 50000 توکن (50 پیام) 80,000 هزارتومان")
                                    .callbackData("/pay/1").build()))
                            .addRow(List.of(InlineKeyboardButton.builder().text("یکماهه روزانه 100000 توکن (100 پیام) 100,000 هزارتومان")
                                    .callbackData("/pay/2").build()))
                            .addRow(List.of(InlineKeyboardButton.builder().text("یکماهه روزانه 150000 توکن (150 پیام) 120,000 هزارتومان")
                                    .callbackData("/pay/3").build()))
                            .build();
                    outgoingTextMessage.setReplyMarkup(inlineKeyboardMarkup);
                    exchange.getMessage().setBody(outgoingTextMessage);
                });
    }
}
