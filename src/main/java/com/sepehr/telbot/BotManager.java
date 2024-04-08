package com.sepehr.telbot;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class BotManager extends RouteBuilder {

    @Value("${camel.telegram.proxy.enable}")
    private boolean telegramProxyEnable;

    @Value("${camel.telegram.proxy.config}")
    private String telegramProxyConfig;

    @Override
    public void configure() {
        final String telegramUri = "telegram:bots" + (telegramProxyEnable ? telegramProxyConfig : "");
        from(telegramUri)
                .choice()
                .when(exchange -> exchange.getMessage().getHeaders().isEmpty()).to("direct:buttons")
                .when(body().isEqualTo("/start")).to("direct:start")
                .when(body().isEqualTo("/help")).to("direct:help").endChoice().end()
                .to(telegramUri);
    }

}
