package com.sepehr.telbot.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.Command;
import com.sepehr.telbot.model.VoiceToTextModel;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.entity.Model;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import com.sepehr.telbot.service.QueueService;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.vertx.http.VertxHttpConstants;
import org.springframework.stereotype.Component;

@Component
public class QueueProcessingRouteBuilder extends AbstractRouteBuilder {

    private final QueueService queueService;

    private final ActiveChatRepository activeChatRepository;

    protected QueueProcessingRouteBuilder(ApplicationConfiguration applicationConfiguration,
                                          QueueService queueService,
                                          ActiveChatRepository activeChatRepository) {
        super(applicationConfiguration);
        this.queueService = queueService;
        this.activeChatRepository = activeChatRepository;
    }

    @Override
    public void configureOtherRoutes() {
        from("timer:queue?period=500")
                .choice().when(exchange -> queueService.peekVoiceToTextModel() != null)
                .to("log:incomeQueue")
                .process(exchange -> {
                    VoiceToTextModel voiceToTextModel = queueService.peekVoiceToTextModel();
                    final String filePath = voiceToTextModel.getGetUrl();
                    final String chatId = voiceToTextModel.getChatId();
                    final ActiveChat activeChat = activeChatRepository.findById(queueService.peekVoiceToTextModel().getChatId()).get();
                    exchange.getMessage().setHeader(ApplicationConfiguration.ACTIVE_CHAT, activeChat);
                    exchange.getMessage().setHeader(ApplicationConfiguration.FILE_PATH, filePath);
                    exchange.getMessage().setHeader(TelegramConstants.TELEGRAM_CHAT_ID, chatId);
                })
                .setBody(simple(null))
                .setHeader("Authorization", constant(applicationConfiguration.getReplicateKey()))
                .setHeader(VertxHttpConstants.HTTP_METHOD, constant("GET"))
                .to("log:toReplicateSpeechToText")
                .toD("vertx-http:${header.filePath}")
                .to("log:fromReplicateSpeechToText")
                .process(exchange -> {
                    JsonNode body = exchange.getMessage().getBody(JsonNode.class);
                    final String status = body.get("status").asText();
                    if (status.equals("succeeded") && queueService.peekVoiceToTextModel() != null) {
                        final String text = body.get("output").get("transcription").asText();
                        final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
                        VoiceToTextModel voiceToTextModel = queueService.pollVoiceToTextModel();
                        AppIncomingReq incomingReq = new AppIncomingReq();
                        incomingReq.setBody(text);
                        incomingReq.setMessageId(voiceToTextModel.getMessageId());
                        if (activeChat.getUsingModel().equals(Model.GPT4)) {
                            activeChat.setBalance(activeChat.getBalance() - Command.VOICE.getUsingBalance());
                            activeChatRepository.save(activeChat);
                        }
                        exchange.getMessage().setBody(incomingReq);
                    } else if (queueService.peekVoiceToTextModel() != null &&
                            System.currentTimeMillis() - queueService.peekVoiceToTextModel().getCreatedAt() >= applicationConfiguration.getReplicateMaxWait()) {
                        queueService.pollVoiceToTextModel();
                        exchange.getMessage().setHeader("voiceUnavailable", true);
                    }
                })
                .choice().when(exchange -> exchange.getMessage().getBody() instanceof AppIncomingReq)
                .to("direct:chat")
                .to(applicationConfiguration.getTelegramUri())
                .when(exchange -> exchange.getMessage().getHeaders().containsKey("voiceUnavailable"))
                .process(exchange -> exchange.getMessage().setBody("درحال حاضر امکان پردازش صوت وجود ندارد. لطفا بعدا امتحان کنید."))
                .to(applicationConfiguration.getTelegramUri())
                .end()
                ;
    }
}
