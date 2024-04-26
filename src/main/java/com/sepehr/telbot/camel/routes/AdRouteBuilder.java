package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.vertx.http.VertxHttpConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AdRouteBuilder extends AbstractRouteBuilder {

    private final ActiveChatRepository activeChatRepository;

    public AdRouteBuilder(ApplicationConfiguration applicationConfiguration,
                          ActiveChatRepository activeChatRepository) {
        super(applicationConfiguration);
        this.activeChatRepository = activeChatRepository;
    }

    @Override
    public void configureOtherRoutes() {
        from("direct:ad")
                .end()
                .to("log:ad?showHeaders=true")
                .process(exchange -> {
                    final String body = exchange.getMessage().getBody(String.class);
                    exchange.getMessage().setHeader(ApplicationConfiguration.BODY_MESSAGE, body);
                    List<ActiveChat> allActiveChats = activeChatRepository.findAll();
                    exchange.getMessage().setBody(
                            allActiveChats.stream().map(ActiveChat::getChatId).collect(Collectors.joining(","))
                    );
                })
                .split(bodyAs(String.class).tokenize(","))
                .parallelProcessing()
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
                    body.put("caption", bodyMessage);
                    body.put("parse_mode", "markdown");
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
                    outgoingTextMessage.setText(bodyMessage);
                    outgoingTextMessage.setChatId(chatId);
                    outgoingTextMessage.setParseMode("markdown");
                    exchange.getMessage().setBody(outgoingTextMessage);
                    exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
                })
                .removeHeader(ApplicationConfiguration.BODY_MESSAGE)
                .to(applicationConfiguration.getTelegramUri())
                .end();
    }
}
