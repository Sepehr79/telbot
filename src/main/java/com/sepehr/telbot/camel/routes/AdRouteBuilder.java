package com.sepehr.telbot.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.sepehr.telbot.config.ApplicationConfiguration;
import io.undertow.util.URLUtils;
import lombok.RequiredArgsConstructor;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.apache.camel.component.telegram.model.OutgoingPhotoMessage;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.net.URLEncoder;

@Component
@RequiredArgsConstructor
public class AdRouteBuilder extends AbstractRouteBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    @Override
    public void configure() throws Exception {
        from("direct:ad")
                .to("log:ad?showHeaders=true")
                .process(exchange -> {
                    IncomingMessage incomingMessage = exchange.getMessage().getBody(IncomingMessage.class);
                    if (incomingMessage.getPhoto() != null) {
                        exchange.getMessage().setHeader(ApplicationConfiguration.PHOTO_ID, incomingMessage.getPhoto().get(
                                incomingMessage.getPhoto().size() - 1
                        ).getFileId());
                    }
                })
                .choice()
                .when(exchange -> exchange.getMessage().getHeaders().containsKey(ApplicationConfiguration.PHOTO_ID))
                    .setBody(exchange -> null)
                    .toD("vertx-http:" + applicationConfiguration.getFileIdApi("${header.photoId}"))
                    .process(exchange -> {
                        JsonNode body = exchange.getMessage().getBody(JsonNode.class);
                        String filePath = body.get("result").get("file_path").textValue();
                        exchange.getMessage().setHeader(ApplicationConfiguration.PHOTO_PATH, filePath);
                    })
                    .setBody(exchange -> null)
                    .toD("vertx-http:" + applicationConfiguration.getFilePathApi("${header.photoPath}"))
                .end()
                .process(exchange -> {

                    byte[] body = (byte[]) exchange.getMessage().getBody();
                    final String text = exchange.getMessage().getHeader(ApplicationConfiguration.BODY_MESSAGE, String.class)
                            .substring(3);
                    OutgoingPhotoMessage outgoingPhotoMessage = new OutgoingPhotoMessage();
                    outgoingPhotoMessage.setPhoto(body);
                    outgoingPhotoMessage.setFilenameWithExtension("photo");
                    outgoingPhotoMessage.setCaption(text.replaceAll("\n", "\n\t"));
                    exchange.getMessage().setBody(outgoingPhotoMessage);
                })
                .to("log:fileResponse?showHeaders=true")
                ;
    }
}
