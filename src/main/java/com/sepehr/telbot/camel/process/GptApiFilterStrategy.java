package com.sepehr.telbot.camel.process;

import org.apache.camel.Exchange;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.springframework.stereotype.Component;

@Component
public class GptApiFilterStrategy implements HeaderFilterStrategy {
    @Override
    public boolean applyFilterToCamelHeaders(String headerName, Object headerValue, Exchange exchange) {
        return true;
    }

    @Override
    public boolean applyFilterToExternalHeaders(String headerName, Object headerValue, Exchange exchange) {
        return headerName.matches("(Authorization|Content-Type)");
    }
}
