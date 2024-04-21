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
        IncomingMessage message = exchange.getMessage().getBody(IncomingMessage.class);
        if (message.getText().startsWith("//")) {
            message.setText(message.getText().substring(2));
            exchange.getMessage().setBody(message);
        } else {
            exchange.getMessage().setHeader(ApplicationConfiguration.ROUTE_SELECT, "ignore");
        }
    }
}
