package com.sepehr.telbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Value("${camel.telegram.proxy.enable}")
    private boolean telegramProxyEnable;

    @Value("${camel.telegram.proxy.config}")
    private String telegramProxyConfig;

    @Value("${openai.url}")
    private String chatGptUrl;

    @Value("${openai.apikey}")
    private String openaiKey;

    public static String MESSAGE_ID = "MessageId";

    public String getTelegramUri() {
        return "telegram:bots" + (telegramProxyEnable ? telegramProxyConfig : "");
    }

    public String getChatGptUrl() {
        return chatGptUrl;
    }

    public String getOpenaiKey() {
        return openaiKey;
    }

}
