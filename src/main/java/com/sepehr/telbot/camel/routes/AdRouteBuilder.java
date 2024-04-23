package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.vertx.http.VertxHttpConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdRouteBuilder extends AbstractRouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    private final ActiveChatRepository activeChatRepository;

    @Value("${telegram.admin.chatId}")
    private String adminId;

    @Override
    public void configure() {
        from("direct:ad")
                .choice()
                    .when(exchange ->
                            !exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class)
                                    .equals(adminId))
                    .to("direct:ignore")
                .end()
                .to("log:ad?showHeaders=true")
                .process(exchange -> {
                    List<ActiveChat> allActiveChats = activeChatRepository.findAll();
                    exchange.getMessage().setBody(
                            allActiveChats.stream().map(ActiveChat::getChatId).collect(Collectors.joining(","))
                    );
                })
                .split(bodyAs(String.class).tokenize(","))
                .to("direct:reply")
                .end()
                .stop();

                from("direct:reply")
                .choice()
                .when(exchange -> exchange.getMessage().getHeaders().containsKey(ApplicationConfiguration.FILE_ID))
                    .process(exchange -> {
                        final String chatId = exchange.getMessage().getBody(String.class);
                        final String photoId = exchange.getMessage().getHeader(ApplicationConfiguration.FILE_ID, String.class);
                        final String bodyMessage = exchange.getMessage().getHeader(ApplicationConfiguration.BODY_MESSAGE, String.class);
                        Map<String, String> body = new HashMap<>();
                        body.put("chat_id", chatId);
                        body.put("photo", photoId);
                        body.put("caption", bodyMessage.substring(3));
                        exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
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
                        final String chatId = exchange.getMessage().getBody(String.class);
                        OutgoingTextMessage outgoingTextMessage = new OutgoingTextMessage();
                        outgoingTextMessage.setText(bodyMessage.substring(3));
                        outgoingTextMessage.setChatId(chatId);
                        outgoingTextMessage.setParseMode("MARKDOWN");
                        exchange.getMessage().setBody(outgoingTextMessage);
                        exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
                    })
                    .to(applicationConfiguration.getTelegramUri())
                .end();
    }
}
