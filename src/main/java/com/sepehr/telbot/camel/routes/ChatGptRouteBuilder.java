package com.sepehr.telbot.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.config.UserProperties;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.Command;
import com.sepehr.telbot.model.GptMessage;
import com.sepehr.telbot.model.GptRequestBuilder;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.entity.Model;
import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import com.sepehr.telbot.service.RedisService;
import io.vertx.core.http.HttpHeaders;
import org.apache.camel.Exchange;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.vertx.http.VertxHttpConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChatGptRouteBuilder extends AbstractRouteBuilder {

    private final GptRequestBuilder gptRequestBuilder;

    private final UserProfileRepository userProfileRepository;

    private final ActiveChatRepository activeChatRepository;

    private final RedisService redisService;

    private final UserProperties userProperties;


    public ChatGptRouteBuilder(ApplicationConfiguration applicationConfiguration,
                               GptRequestBuilder gptRequestBuilder,
                               UserProfileRepository userProfileRepository,
                               RedisService redisService,
                               ActiveChatRepository activeChatRepository,
                               UserProperties userProperties) {
        super(applicationConfiguration);
        this.gptRequestBuilder = gptRequestBuilder;
        this.userProfileRepository = userProfileRepository;
        this.redisService = redisService;
        this.activeChatRepository = activeChatRepository;
        this.userProperties = userProperties;
    }

    @Override
    public void configureOtherRoutes() {

        from("direct:chat")
                .to("log:chat?showHeaders=true")
                .to("seda:typingAction")
                .process(exchange -> {
                    final var body = exchange.getMessage().getBody(AppIncomingReq.class);
                    final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
                    final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                    final UserProfile userProfile = userProfileRepository.findById(chatId).orElseGet(() ->
                            UserProfile.builder().id(chatId)
                                    .gptReq(gptRequestBuilder.createGptReq(activeChat.getUsingModel()))
                                    .lastCall(0)
                                    .build());
                    final GptMessage gptMessage = gptRequestBuilder.createUserMessage(body.getBody());
                    redisService.pushMessage(body.getBody());

                    if (System.currentTimeMillis() - userProfile.getLastCall() >= userProperties.getModel().get(activeChat.getUsingModel()).getPeriod()) {
                        userProfile.setLastCall(System.currentTimeMillis());
                    } else {
                        exchange.getMessage().setHeader(ApplicationConfiguration.CHAT_PERIOD_PER, true);
                    }

                    userProfile.getGptReq().getMessages().add(gptMessage);
                    exchange.getMessage().setHeader(ApplicationConfiguration.BODY_MESSAGE, body.getMessageId());
                    exchange.getMessage().setHeader(ApplicationConfiguration.USER_PROFILE, userProfile);
                    exchange.getMessage().setBody(userProfile.getGptReq());
                })
                .choice()
                .when(exchange -> exchange.getMessage().getHeaders().containsKey(ApplicationConfiguration.CHAT_PERIOD_PER))
                    .to("direct:per")
                .end()
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader(VertxHttpConstants.HTTP_METHOD, constant("POST"))
                .setHeader("Authorization", constant(applicationConfiguration.getOpenaiKey()))
                .setHeader(HttpHeaders.USER_AGENT.toString(), constant("GPTtelbot"))
                .marshal().json(JsonLibrary.Jackson)
                .to("log:toGpt?showHeaders=true")
                .to(applicationConfiguration.getChatGptUrl())
                .to("log:fromGpt?showHeaders=true")
                .process(exchange -> {
                    JsonNode bodyResult = exchange.getMessage().getBody(JsonNode.class);
                    final String body = bodyResult.get("choices").get(0).get("message").get("content").asText();
                    final Integer messageId = exchange.getMessage().getHeader(ApplicationConfiguration.BODY_MESSAGE, Integer.class);
                    final UserProfile userProfile = exchange.getMessage().getHeader(ApplicationConfiguration.USER_PROFILE, UserProfile.class);
                    final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
                    userProfile.getGptReq().getMessages().add(gptRequestBuilder.createAssistantMessage(body));
                    userProfileRepository.save(userProfile);
                    final OutgoingTextMessage outMessage = OutgoingTextMessage.builder()
                            .text(body)
                            .build();
                    outMessage.setReplyToMessageId(messageId.longValue());
                    outMessage.setParseMode("markdown");
                    activeChat.setBalance(activeChat.getBalance() - userProperties.getModel().get(activeChat.getUsingModel()).getChatCost());
                    activeChatRepository.save(activeChat);
                    exchange.getMessage().setBody(outMessage);
                });

        from("seda:typingAction")
                .process(exchange -> {
                    final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                    Map<String, String> body = new HashMap<>();
                    body.put("chat_id", chatId);
                    body.put("action", "typing");
                    exchange.getMessage().setBody(body);
                })
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(VertxHttpConstants.HTTP_METHOD, constant("POST"))
                .setHeader(VertxHttpConstants.CONTENT_TYPE, constant("application/json"))
                .to(applicationConfiguration.getChatActionApi())
        ;

        from("direct:per")
                .process(exchange -> {
                    final long lastCall         = exchange.getMessage().getHeader(ApplicationConfiguration.USER_PROFILE, UserProfile.class).getLastCall();
                    final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
                    final long period           = userProperties.getModel().get(activeChat.getUsingModel()).getPeriod();
                    final long remainingTime    = period - (System.currentTimeMillis() - lastCall);
                    exchange.getMessage().setBody(
                            String.format("شما هر %d ثانیه مجاز به ارسال یک پیام هستید." +
                                    "لطفا %d ثانیه دیگر امتحان کنید.",
                                    period / 1000,
                                    remainingTime / 1000));
                }).to(applicationConfiguration.getTelegramUri()).stop();

    }
}
