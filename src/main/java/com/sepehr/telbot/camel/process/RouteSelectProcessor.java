package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.Command;
import com.sepehr.telbot.model.AppIncomingReq;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RouteSelectProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        final AppIncomingReq bodyMessage = exchange.getMessage().getBody(AppIncomingReq.class);
        String routeCommand = Arrays.stream(Command.values())
                .filter(command -> bodyMessage.getBody().startsWith("/" + command.toString().toLowerCase()))
                .findFirst()
                .map(command -> command.toString().toLowerCase())
                .orElse("chat");
        exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, routeCommand);
    }
}
