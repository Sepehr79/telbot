package com.sepehr.telbot.camel.process.command;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.Command;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupCommandProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        String body = exchange.getMessage().getBody(String.class);
        if (body.startsWith("//")) {
            body = body.substring(2);

            exchange.getMessage().setBody(body);
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, Command.CHAT.toString().toLowerCase());
        } else {
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "ignore");
        }
    }
}
