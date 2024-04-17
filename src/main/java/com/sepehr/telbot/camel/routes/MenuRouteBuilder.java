package com.sepehr.telbot.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MenuRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:start")
                .transform(constant("سلام. این ربات بهت کمک میکنه که مستقیم با ChatGPT صحبت کنی." +
                        "\n/start  مشاهده منو \n" +
                        "/chat  صحبت با ربات \n" +
                        "/contact  پیام ناشناس به توسعه دهنده"))
                .process();
    }
}
