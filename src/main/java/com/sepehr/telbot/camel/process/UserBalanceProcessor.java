package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.Command;
import com.sepehr.telbot.model.entity.ActiveChat;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class UserBalanceProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
        String route = exchange.getMessage().getHeader(ApplicationConfiguration.ROUTE_SELECT, String.class);
        if (route.equals("chat") || route.equals("voice")) {
            long usingBalance = route.equals("chat") ? Command.CHAT.getUsingBalance() : Command.CHAT.getUsingBalance() + Command.VOICE.getUsingBalance();
            if (activeChat.getBalance() - usingBalance < 0) {
                route = Command.BALANCE.toString().toLowerCase();
                final String body = "موجودی شما ناکافی است. لطفا حساب خود را شارژ کنید.";
                exchange.getMessage().setHeader(ApplicationConfiguration.BALANCE_MESSAGE, body);
                exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, route);
            }
        }
    }
}
