package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.ActiveChat;
import org.springframework.stereotype.Component;

@Component
public class BalanceRouteBuilder extends AbstractRouteBuilder {
    protected BalanceRouteBuilder(ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration);
    }

    @Override
    public void configureOtherRoutes() {
        from("direct:balance")
                .process(exchange -> {
                    final ActiveChat activeChat = exchange.getMessage().getHeader(ApplicationConfiguration.ACTIVE_CHAT, ActiveChat.class);
                    final String body = exchange.getMessage().getHeader(ApplicationConfiguration.BALANCE_MESSAGE, String.class);
                    final String balance = String.format("موجودی فعلی شما به میزان %d توکن می باشد.", activeChat.getBalance());
                    final String message = body == null ? balance : body + "\n" + balance;
                    exchange.getMessage().setBody(message);
                });
    }
}
