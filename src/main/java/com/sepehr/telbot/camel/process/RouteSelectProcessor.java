package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.Command;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RouteSelectProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        final String bodyMessage = exchange.getMessage().getHeader(ApplicationConfiguration.BODY_MESSAGE, String.class);
        String routeCommand = Arrays.stream(Command.values())
                .filter(command -> bodyMessage.startsWith("/" + command.toString().toLowerCase()))
                .findFirst()
                .map(command -> command.toString().toLowerCase())
                .orElse("chat");
        exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, routeCommand);
    }
}
