package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.vertx.http.VertxHttpConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdRouteBuilder extends AbstractRouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    @Override
    public void configure() {
        from("direct:ad")
                .to("log:ad?showHeaders=true")
                .process(exchange -> {
                    IncomingMessage incomingMessage = exchange.getMessage().getBody(IncomingMessage.class);
                    if (incomingMessage.getPhoto() != null) {
                        exchange.getMessage().setHeader(ApplicationConfiguration.PHOTO_ID, incomingMessage.getPhoto().get(
                                incomingMessage.getPhoto().size() - 1
                        ).getFileId());
                    }
                })
                .choice()
                .when(exchange -> exchange.getMessage().getHeaders().containsKey(ApplicationConfiguration.PHOTO_ID))
                    .process(exchange -> {
                        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                        final String photoId = exchange.getMessage().getHeader(ApplicationConfiguration.PHOTO_ID, String.class);
                        final String bodyMessage = exchange.getMessage().getHeader(ApplicationConfiguration.BODY_MESSAGE, String.class);
                        Map<String, String> body = new HashMap<>();
                        body.put("chat_id", chatId);
                        body.put("photo", photoId);
                        body.put("caption", bodyMessage.substring(4));
                        exchange.getMessage().setBody(body);
                    })
                    .marshal().json(JsonLibrary.Jackson)
                    .removeHeader(ApplicationConfiguration.BODY_MESSAGE)
                    .setHeader(VertxHttpConstants.CONTENT_TYPE, constant("application/json"))
                    .setHeader(VertxHttpConstants.HTTP_METHOD, constant("POST"))
                    .to("vertx-http:" + applicationConfiguration.getPhotoSendApi())
                .otherwise()
                    .process(exchange -> {
                        final String bodyMessage = exchange.getMessage().getHeader(ApplicationConfiguration.BODY_MESSAGE, String.class);
                        final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                        OutgoingTextMessage outgoingTextMessage = new OutgoingTextMessage();
                        outgoingTextMessage.setText(bodyMessage.substring(4));
                        outgoingTextMessage.setChatId(chatId);
                        outgoingTextMessage.setParseMode("MARKDOWN");
                        exchange.getMessage().setBody(outgoingTextMessage);
                    })
                    .to(applicationConfiguration.getTelegramUri())
                .end()
                .to("log:fileResponse?showHeaders=true")
                .stop()
                ;
    }
}
