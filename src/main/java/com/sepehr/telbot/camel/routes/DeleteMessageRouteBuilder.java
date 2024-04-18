package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.EditMessageDelete;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteMessageRouteBuilder extends RouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    @Override
    public void configure() {
        from("seda:deleteMessage")
                .process(exchange -> {
                    EditMessageDelete editMessageDelete = exchange.getMessage().getHeader("DeleteMessage", EditMessageDelete.class);
                    exchange.getMessage().setBody(editMessageDelete);
                }).to(applicationConfiguration.getTelegramUri());
    }
}
