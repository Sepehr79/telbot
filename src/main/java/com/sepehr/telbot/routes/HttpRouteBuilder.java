package com.sepehr.telbot.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HttpRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("netty-http:http://0.0.0.0:{{camel.netty.proxy.port}}{{camel.netty.proxy.path}}")
                .to("log:web?showHeaders=true");
    }
}
