package com.sepehr.telbot.camel.process.command;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.Command;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommandProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        String body = exchange.getMessage().getBody(String.class);

        Command userCommand;
        try {
            userCommand = Command.valueOf(body.substring(1).toUpperCase());
        } catch (IllegalArgumentException illegalArgumentException) {
            userCommand = Command.START;
        }

        exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, userCommand.toString().toLowerCase());
        exchange.getMessage().setBody(body);
    }
}
