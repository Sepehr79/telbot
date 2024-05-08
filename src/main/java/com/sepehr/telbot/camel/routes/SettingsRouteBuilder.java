package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.config.UserProperties;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.entity.ActiveChat;
import com.sepehr.telbot.model.entity.Model;
import com.sepehr.telbot.model.repo.ActiveChatRepository;
import org.apache.camel.component.telegram.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettingsRouteBuilder extends AbstractRouteBuilder {

    private final UserProperties userProperties;

    private final ActiveChatRepository activeChatRepository;

    protected SettingsRouteBuilder(ApplicationConfiguration applicationConfiguration,
                                   UserProperties userProperties,
                                   ActiveChatRepository activeChatRepository) {
        super(applicationConfiguration);
        this.userProperties = userProperties;
        this.activeChatRepository = activeChatRepository;
    }

    @Override
    public void configureOtherRoutes() {
        from("direct:settings")
                .process(exchange -> {
                    final String botSettingsText = "تنظیمات ربات\n\nدر این بخش شما میتوانید مدل انتخابی خود را مشخص کنید.";
                    AppIncomingReq body = exchange.getMessage().getBody(AppIncomingReq.class);
                    final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
                    OutgoingMessage outgoingMessage;
                    if (body.getBody().equals("/settings/changeModel")) {
                        activeChat.setUsingModel(activeChat.getUsingModel().equals(Model.GPT35) ? Model.GPT4 : Model.GPT35);
                        activeChatRepository.save(activeChat);
                        outgoingMessage = EditMessageTextMessage.builder()
                                .text(botSettingsText)
                                .replyMarkup(getInlineKeyboardMarkup(activeChat))
                                .messageId(body.getMessageId())
                                .build();
                    } else {
                        outgoingMessage = OutgoingTextMessage.builder()
                                .text(botSettingsText)
                                .replyMarkup(getInlineKeyboardMarkup(activeChat))
                                .parseMode("markdown")
                                .build();
                    }
                    exchange.getMessage().setBody(outgoingMessage);
                });
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(ActiveChat activeChat) {
        return InlineKeyboardMarkup.builder()
                        .addRow(List.of(InlineKeyboardButton.builder().text(String.format("مدل: %s", userProperties.getModel().get(activeChat.getUsingModel()).getName()))
                                .callbackData("/settings/changeModel").build()))
                        .build();
    }
}
