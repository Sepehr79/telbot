package com.sepehr.telbot.model;

import lombok.Data;

@Data
public class VoiceToTextModel {

    private final String getUrl;

    private final String chatId;

    private final Integer messageId;

    private final long createdAt;

}
