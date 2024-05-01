package com.sepehr.telbot.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.VoiceToTextModel;
import com.sepehr.telbot.service.QueueService;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.vertx.http.VertxHttpConstants;
import org.springframework.stereotype.Component;

@Component
public class QueueProcessingRouteBuilder extends AbstractRouteBuilder {

    private final QueueService queueService;

    protected QueueProcessingRouteBuilder(ApplicationConfiguration applicationConfiguration,
                                          QueueService queueService) {
        super(applicationConfiguration);
        this.queueService = queueService;
    }

    @Override
    public void configureOtherRoutes() {
        from("timer:queue?period=500")
                .choice().when(exchange -> queueService.peekVoiceToTextModel() != null)
                .process(exchange -> {
                    VoiceToTextModel voiceToTextModel = queueService.peekVoiceToTextModel();
                    final String filePath = voiceToTextModel.getGetUrl();
                    final String chatId = voiceToTextModel.getChatId();
                    exchange.getMessage().setHeader(ApplicationConfiguration.FILE_PATH, filePath);
                    exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
                })
                .setBody(simple(null))
                .setHeader("Authorization", constant(applicationConfiguration.getReplicateKey()))
                .setHeader(VertxHttpConstants.HTTP_METHOD, constant("GET"))
                .toD("vertx-http:${header.filePath}")
                .process(exchange -> {
                    JsonNode body = exchange.getMessage().getBody(JsonNode.class);
                    final String status = body.get("status").asText();
                    if (status.equals("succeeded") && queueService.peekVoiceToTextModel() != null) {
                        final String text = body.get("output").get("transcription").asText();
                        VoiceToTextModel voiceToTextModel = queueService.pollVoiceToTextModel();
                        AppIncomingReq incomingReq = new AppIncomingReq();
                        incomingReq.setBody(text);
                        incomingReq.setMessageId(voiceToTextModel.getMessageId());
                        exchange.getMessage().setBody(incomingReq);
                    }
                })
                .choice().when(exchange -> exchange.getMessage().getBody() instanceof AppIncomingReq)
                .to("direct:chat")
                .to(applicationConfiguration.getTelegramUri())
                .end()
                ;
    }
}
