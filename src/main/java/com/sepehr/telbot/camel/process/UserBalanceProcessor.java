package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.config.UserProperties;
import com.sepehr.telbot.model.Command;
import com.sepehr.telbot.model.entity.ActiveChat;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBalanceProcessor implements Processor {

    private final UserProperties userProperties;

    @Override
    public void process(Exchange exchange) throws Exception {
        final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
        String route = exchange.getMessage().getHeader(ApplicationConfiguration.ROUTE_SELECT, String.class);
        if (route.equals("chat") || route.equals("voice")) {
            long usingBalance = route.equals("chat") ? userProperties.getModel().get(activeChat.getUsingModel()).getChatCost() :
                    userProperties.getModel().get(activeChat.getUsingModel()).getChatCost() + userProperties.getModel().get(activeChat.getUsingModel()).getVoiceCost();
            if (activeChat.getBalance() - usingBalance < 0) {
                route = Command.BALANCE.toString().toLowerCase();
                exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, route);
            }
        }
    }
}
