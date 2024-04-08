package com.sepehr.telbot.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.IncomingCallbackQuery;
import org.springframework.stereotype.Component;

@Component
public class ButtonsRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {

        from("direct:buttons")
                .to("log:buttons?showHeaders=true")
                .process(exchange -> {
                    IncomingCallbackQuery incomingCallbackQuery = (IncomingCallbackQuery) exchange.getMessage().getBody();
                    exchange.getMessage().setHeader("CamelTelegramChatId",  incomingCallbackQuery.getFrom().getId());
                    exchange.getMessage().setBody("Thanks");
                })
                .to("direct:payment");
    }
}
