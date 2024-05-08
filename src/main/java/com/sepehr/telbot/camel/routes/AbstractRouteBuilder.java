package com.sepehr.telbot.camel.routes;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.AppIncomingReq;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.EditMessageTextMessage;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.apache.camel.component.telegram.model.OutgoingMessage;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.http.base.HttpOperationFailedException;

public abstract class AbstractRouteBuilder extends RouteBuilder {

    public final ApplicationConfiguration applicationConfiguration;

    protected AbstractRouteBuilder(final ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public void configure() {
        onException(RuntimeCamelException.class)
                .handled(true)
                .useOriginalMessage()
                .to("log:exc?showHeaders=true");

        onException(HttpOperationFailedException.class)
                .handled(true)
                .useOriginalMessage()
                .process(exchange -> {
                    HttpOperationFailedException caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
                    if (caused.getStatusCode() == 429) {
                        exchange.getMessage().setBody("در حال حاضر افراد زیادی از این ربات استفاده میکنند. " +
                                "لطفا چند دقیقه دیگر دوباره امتحان کنید.");
                        exchange.getMessage().setHeader("Http429", true);
                    }
                })
                .choice().when(exchange -> exchange.getMessage().getHeaders().containsKey("Http429"))
                .to(applicationConfiguration.getTelegramUri());
        configureOtherRoutes();
    }

    public abstract void configureOtherRoutes();

    public OutgoingMessage getOutGoingTextMessageBuilder(final Exchange exchange, final String text, final InlineKeyboardMarkup replyMarkup) {
        AppIncomingReq telegramIncomingReq = exchange.getMessage().getBody(AppIncomingReq.class);
        if (telegramIncomingReq.getIncomingCallbackQuery() != null) {
            return EditMessageTextMessage.builder()
                    .text(text)
                    .replyMarkup(replyMarkup)
                    .messageId(telegramIncomingReq.getMessageId())
                    .build();
        }
        return OutgoingTextMessage.builder()
                .text(text)
                .replyMarkup(replyMarkup)
                .parseMode("markdown")
                .build();
    }

}
