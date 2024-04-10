package com.sepehr.telbot.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HelpSupportRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:help").process();
    }
}
