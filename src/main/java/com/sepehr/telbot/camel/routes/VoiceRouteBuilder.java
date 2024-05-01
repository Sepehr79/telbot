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
                .toD("vertx-http:" + applicationConfiguration.getFileIdApi("${header.fileId}"))
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
                .to("vertx-http:" + applicationConfiguration.getReplicateUrl())
                .process(exchange -> {
                    JsonNode body = exchange.getMessage().getBody(JsonNode.class);
                    String getUrl = body.get("urls").get("get").asText();
                    exchange.getMessage().setHeader(ApplicationConfiguration.FILE_PATH, getUrl);
                })
                .setBody(simple(null))
                .setHeader("Authorization", constant(applicationConfiguration.getReplicateKey()))
                .setHeader(VertxHttpConstants.HTTP_METHOD, constant("GET"))
                .toD("vertx-http:${header.filePath}")
                .process(exchange -> {
                    final String getUrl = exchange.getMessage().getHeader(ApplicationConfiguration.FILE_PATH, String.class);
                    final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                    final Integer messageId = exchange.getMessage().getHeader(ApplicationConfiguration.REPLY_MESSAGE_ID, Integer.class);
                    queueService.appendVoiceToTextModel(new VoiceToTextModel(getUrl, chatId, messageId));
                    exchange.getMessage().setBody(null);
                })
                .to("log:endd")
        ;
    }
}

//curl -s -X POST   -H "Authorization: Bearer r8_3GVgrrydfN7KH6m1PcraJGV733e8HGI47jTGR"   -H "Content-Type: application/json"   -d '{
//        "version": "4d50797290df275329f202e48c76360b3f22b08d28c196cbc54600319435f8d2",
//        "input": {
//        "audio": "https://api.telegram.org/file/bot6732521374:AAF8ogVAEVqmO70PmukFwQqccqRFeCWdEU4/voice/file_31.oga"
//        }
//        }'   https://api.replicate.com/v1/predictions

//{"id":"06h9xhxewxrgc0cf6c9s0shqsm","model":"openai/whisper","version":"4d50797290df275329f202e48c76360b3f22b08d28c196cbc54600319435f8d2","input":{"audio":"https://api.telegram.org/file/bot6732521374:AAF8ogVAEVqmO70PmukFwQqccqRFeCWdEU4/voice/file_31.oga"},"logs":"","error":null,"status":"starting","created_at":"2024-05-01T07:34:39.079Z","urls":{"cancel":"https://api.replicate.com/v1/predictions/06h9xhxewxrgc0cf6c9s0shqsm/cancel","get":"https://api.replicate.com/v1/predictions/06h9xhxewxrgc0cf6c9s0shqsm"}}

//curl -X GET   -H "Authorization: Bearer r8_3GVgrrydfN7KH6m1PcraJGV733e8HGI47jTGR"   -H "Content-Type: application/json" https://api.replicate.com/v1/predictions/87ghfs40esrga0cf6c89b9w7zg

//{"id":"87ghfs40esrga0cf6c89b9w7zg","model":"openai/whisper","version":"4d50797290df275329f202e48c76360b3f22b08d28c196cbc54600319435f8d2","input":{"audio":"https://api.telegram.org/file/bot6732521374:AAF8ogVAEVqmO70PmukFwQqccqRFeCWdEU4/voice/file_31.oga"},"logs":"Transcribe with large-v3 model.\nDetected language: Persian\n  0%|          | 0/329 [00:00\u003c?, ?frames/s]\n100%|██████████| 329/329 [00:00\u003c00:00, 403.82frames/s]\n100%|██████████| 329/329 [00:00\u003c00:00, 403.72frames/s]\n","output":{"detected_language":"persian","segments":[{"avg_logprob":-0.25996342301368713,"compression_ratio":0.6538461538461539,"end":3.3000000000000003,"id":0,"no_speech_prob":0.004739189520478249,"seek":0,"start":0,"temperature":0,"text":" سلام خوبی","tokens":[50365,8608,37440,16490,37746,4135,50530]}],"transcription":" سلام خوبی","translation":null},"error":null,"status":"succeeded","created_at":"2024-05-01T07:31:10.582Z","started_at":"2024-05-01T07:31:29.283918Z","completed_at":"2024-05-01T07:31:31.267828Z","urls":{"cancel":"https://api.replicate.com/v1/predictions/87ghfs40esrga0cf6c89b9w7zg/cancel","get":"https://api.replicate.com/v1/predictions/87ghfs40esrga0cf6c89b9w7zg"},"metrics":{"predict_time":1.98391}}

