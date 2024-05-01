package com.sepehr.telbot.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.VoiceToTextModel;
import com.sepehr.telbot.service.QueueService;
import org.apache.camel.Exchange;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.vertx.http.VertxHttpConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class VoiceRouteBuilder extends AbstractRouteBuilder {

    private final QueueService queueService;

    protected VoiceRouteBuilder(ApplicationConfiguration applicationConfiguration,
                                QueueService queueService) {
        super(applicationConfiguration);
        this.queueService = queueService;
    }

    @Override
    public void configureOtherRoutes() {
        from("direct:voice")
                .to("log:voice?showHeaders=true")
                .process(exchange -> {
                    final var telegramIncomingReq = exchange.getMessage().getBody(AppIncomingReq.class);
                    final var fileId = telegramIncomingReq.getIncomingMessage().getAudio().getFileId();
                    final var messageId = telegramIncomingReq.getMessageId();
                    exchange.getMessage().setHeader(ApplicationConfiguration.FILE_ID, fileId);
                    exchange.getMessage().setHeader(ApplicationConfiguration.REPLY_MESSAGE_ID, messageId);
                })
                .removeHeader(ApplicationConfiguration.BODY_MESSAGE) // Necessary to prevent IllegalArgumentException
                .setHeader(VertxHttpConstants.CONTENT_TYPE, constant("application/json"))
                .setHeader(VertxHttpConstants.HTTP_METHOD, constant("GET"))
                .setBody(simple(null))
                .toD("vertx-http:" + applicationConfiguration.getFileIdApi("${header.fileId}")) // Get telegram filePath
                .process(exchange -> {
                    JsonNode result = exchange.getMessage().getBody(JsonNode.class);
                    String filePath = result.get("result").get("file_path").asText();
                    final String fileUrl = applicationConfiguration.getFilePathApi(filePath);
                    Map<String, Object> body = new HashMap<>();
                    body.put("version", "4d50797290df275329f202e48c76360b3f22b08d28c196cbc54600319435f8d2");
                    body.put("input", Map.of("audio", fileUrl));
                    exchange.getMessage().setBody(body);
                })
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", constant(applicationConfiguration.getReplicateKey()))
                .setHeader(VertxHttpConstants.HTTP_METHOD, constant("POST"))
                .marshal().json(JsonLibrary.Jackson)
                .to("vertx-http:" + applicationConfiguration.getReplicateUrl()) // translate voice to text
                .process(exchange -> {
                    JsonNode body = exchange.getMessage().getBody(JsonNode.class);
                    String getUrl = body.get("urls").get("get").asText();
                    final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                    final Integer messageId = exchange.getMessage().getHeader(ApplicationConfiguration.REPLY_MESSAGE_ID, Integer.class);
                    queueService.appendVoiceToTextModel(new VoiceToTextModel(getUrl, chatId, messageId));
                })
                .stop()

        ;
    }
}