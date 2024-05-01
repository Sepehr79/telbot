package com.sepehr.telbot.model;

import lombok.Data;
import org.apache.camel.component.telegram.model.IncomingCallbackQuery;
import org.apache.camel.component.telegram.model.IncomingMessage;

@Data
public class AppIncomingReq {

    private IncomingCallbackQuery incomingCallbackQuery;

    private IncomingMessage incomingMessage;

    private String body;

    private Integer messageId;

    private String photoUrl;

    private String chatId;
}
