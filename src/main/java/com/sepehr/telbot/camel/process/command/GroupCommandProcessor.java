package com.sepehr.telbot.camel.process.command;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.Command;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupCommandProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        String bodyMessage = exchange.getMessage().getHeader(ApplicationConfiguration.BODY_MESSAGE, String.class);
        IncomingMessage incomingMessage = exchange.getMessage().getBody(IncomingMessage.class);
        final String routeSelect = exchange.getMessage().getHeader(ApplicationConfiguration.ROUTE_SELECT, String.class);
        if (bodyMessage.startsWith("//")) {
            bodyMessage = bodyMessage.substring(2);
            incomingMessage.setText(bodyMessage);
            exchange.getMessage().setBody(incomingMessage);
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "chat");
        } else if (routeSelect.equals("chat")) {
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "ignore");
        }
    }
}
