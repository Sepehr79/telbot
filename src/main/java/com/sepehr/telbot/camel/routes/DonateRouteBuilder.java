package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DonateRouteBuilder extends AbstractRouteBuilder {

    @Override
    public void configure() {
        from("direct:donate")
                .process(exchange -> {
                    String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                    OutgoingTextMessage outgoingMessage = OutgoingTextMessage.builder()
                            .text("استفاده از این ربات برای شما هیچ هزینه ای نخواهد داشت البته در آینده امکانات بیشتری اضافه خواهیم کرد تا از طریق آنها بتوانیم کسب درامد کنیم.\n\nبا این وجود برای انگیزه بیشتر خوشحال میشیم که از ما حمایت کنید و قدردان توجه شما خواهیم بود.\n\n" +
                                    "BTC:\n `bc1qwvkj4ckplgt6c8f09cxxusym5x9j5uxse2dzh6`\n\n" +
                                    "USDT(TRC20):\n `TQJR3QSpT7Q6MgJPFyKTbhKP9FmfMZdu3E`\n\n" +
                                    "MATIC(POLYGON):\n `0xE791Dd7190F564B0a78c6423bfB2783d19d0b0Dc`\n\n")
                            .parseMode("MARKDOWN")
                            .build();
                    outgoingMessage.setChatId(chatId);
                    exchange.getMessage().setBody(outgoingMessage);
                });
    }
}
