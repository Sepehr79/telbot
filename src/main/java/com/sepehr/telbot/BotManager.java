package com.sepehr.telbot;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */

@Component
public class BotManager extends RouteBuilder {
    @Override
    public void configure() {
        from("telegram:bots?authorizationToken=6732521374:AAF8ogVAEVqmO70PmukFwQqccqRFeCWdEU4")
                .to("log:INFO?showHeaders=true");
    }

}
