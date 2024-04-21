package com.sepehr.telbot.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.GptMessage;
import com.sepehr.telbot.model.entity.GptRequestBuilder;
import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatGptRouteBuilder extends AbstractRouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    private final GptRequestBuilder gptRequestBuilder;

    private final UserProfileRepository userProfileRepository;

    @Override
    public void configure() {

        from("direct:chat")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", constant(applicationConfiguration.getOpenaiKey()))
                .process(exchange -> {
                    final String body = exchange.getMessage().getBody(IncomingMessage.class).getText();
                    final String chatId = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_CHAT_ID, String.class);
                    final UserProfile userProfile = userProfileRepository.findById(chatId).orElseGet(() ->
                            UserProfile.builder().id(chatId)
                                    .gptReq(gptRequestBuilder.createGptReq())
                                    .build());
                    final GptMessage gptMessage = gptRequestBuilder.createUserMessage(body);
                    userProfile.getGptReq().getMessages().add(gptMessage);
                    exchange.getMessage().setHeader(ApplicationConfiguration.USER_PROFILE, userProfile);
                    exchange.getMessage().setBody(userProfile.getGptReq());
                })
                .marshal().json(JsonLibrary.Jackson)
                .to("log:chatGptFinalResult?showHeaders=true")
                .to(applicationConfiguration.getChatGptUrl())
                .to("log:chatGptAnswer?showHeaders=true")
                .process(exchange -> {
                    JsonNode bodyResult = exchange.getMessage().getBody(JsonNode.class);
                    final String body = bodyResult.get("choices").get(0).get("message").get("content").asText();
                    final Integer messageId = exchange.getMessage().getHeader(ApplicationConfiguration.REPLY_MESSAGE_ID, Integer.class);
                    final UserProfile userProfile = exchange.getMessage().getHeader(ApplicationConfiguration.USER_PROFILE, UserProfile.class);
                    final String parseMode = exchange.getMessage().getHeader(TelegramConstants.TELEGRAM_PARSE_MODE, String.class);
                    userProfile.getGptReq().getMessages().add(gptRequestBuilder.createAssistantMessage(body));
                    userProfileRepository.save(userProfile);

                    final OutgoingTextMessage outMessage = OutgoingTextMessage.builder()
                            .text(body)
                            .build();
                    outMessage.setReplyToMessageId(messageId.longValue());
                    outMessage.setParseMode(parseMode);

                    exchange.getMessage().setBody(outMessage);
                });

    }
}
