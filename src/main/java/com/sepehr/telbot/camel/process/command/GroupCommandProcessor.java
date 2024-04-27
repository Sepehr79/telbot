package com.sepehr.telbot.camel.process.command;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupCommandProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        AppIncomingReq bodyMessage = exchange.getMessage().getBody(AppIncomingReq.class);
        final String routeSelect = exchange.getMessage().getHeader(ApplicationConfiguration.ROUTE_SELECT, String.class);
        if (bodyMessage.getBody().startsWith("//")) {
            bodyMessage.setBody(bodyMessage.getBody().substring(2));
            exchange.getMessage().setBody(bodyMessage);
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "chat");
        } else if (routeSelect.equals("chat")) {
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "ignore");
        }
    }
}
