package com.sepehr.telbot.camel.process;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import com.sepehr.telbot.model.Command;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RouteSelectProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        final AppIncomingReq bodyMessage = exchange.getMessage().getBody(AppIncomingReq.class);
        final String routeCommand;
        if (bodyMessage.getIncomingMessage() != null &&
                bodyMessage.getIncomingMessage().getAudio() != null)
            routeCommand = Command.VOICE.toString().toLowerCase();
        else {
            routeCommand = Arrays.stream(Command.values())
                    .filter(command -> bodyMessage.getBody().startsWith("/" + command.toString().toLowerCase()))
                    .findFirst()
                    .map(command -> command.toString().toLowerCase())
                    .orElse(Command.CHAT.toString().toLowerCase());
        }
        exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, routeCommand);
    }
}
