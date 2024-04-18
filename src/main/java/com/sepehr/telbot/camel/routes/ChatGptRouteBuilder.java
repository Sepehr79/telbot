package com.sepehr.telbot.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.GptReq;
import com.sepehr.telbot.model.entity.GptRequestBuilder;
import com.sepehr.telbot.model.entity.UserProfile;
import com.sepehr.telbot.model.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.InlineKeyboardButton;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatGptRouteBuilder extends RouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    private final GptRequestBuilder gptRequestBuilder;

    private final UserProfileRepository userProfileRepository;

    @Override
    public void configure() {

        from("direct:chat")
                .choice()
                .when(exchange -> {
                    final String body = exchange.getMessage().getBody(String.class);
                    return body.equals("/chat");
                }).process(exchange -> {
                    final OutgoingTextMessage outgoingTextMessage = new OutgoingTextMessage();
                    outgoingTextMessage.setText("شما اکنون با ربات صحبت میکنید");

                    InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                            .addRow(List.of(InlineKeyboardButton.builder().text("منو اصلی").callbackData("/start").build()))
                            .addRow(List.of(InlineKeyboardButton.builder().text("پیام ناشناس به توسعه دهنده").callbackData("/contact").build()))
                            .build();

                    outgoingTextMessage.setReplyMarkup(inlineKeyboardMarkup);
                    exchange.getMessage().setBody(outgoingTextMessage);
                })
                .otherwise()
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Authorization", constant(applicationConfiguration.getOpenaiKey()))
                .process(exchange -> {
                    final String body = exchange.getMessage().getBody(String.class);
                    final UserProfile userProfile = exchange.getMessage().getHeader("UserProfile", UserProfile.class);
                    final GptReq gptReq = gptRequestBuilder.createGptReq();
                    if (userProfile.getGptMessages() != null) {
                        gptReq.getMessages().addAll(userProfile.getGptMessages());
                    } else {
                        userProfile.setGptMessages(new ArrayList<>());
                    }
                    gptReq.getMessages().add(gptRequestBuilder.createUserMessage(body));
                    userProfile.getGptMessages().add(gptRequestBuilder.createUserMessage(body));
                    exchange.getMessage().setBody(gptReq);
                })
                .marshal().json(JsonLibrary.Jackson)
                .to("log:chatGptFinalResult?showHeaders=true")
                .to(applicationConfiguration.getChatGptUrl())
                .to("log:chatGptAnswer?showHeaders=true")
                .process(exchange -> {
                    JsonNode bodyResult = exchange.getMessage().getBody(JsonNode.class);
                    final String body = bodyResult.get("choices").get(0).get("message").get("content").asText();

                    final UserProfile userProfile = exchange.getMessage().getHeader("UserProfile", UserProfile.class);
                    userProfile.getGptMessages().add(gptRequestBuilder.createAssistantMessage(body));

                    userProfileRepository.save(userProfile);

                    exchange.getMessage().setBody(body);
                })
                .endChoice().end()
                ;

    }
}
