package com.sepehr.telbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Value("${camel.telegram.proxy.enable}")
    private boolean telegramProxyEnable;

    @Value("${telegram.admin.chatId}")
    private String adminId;

    @Value("${camel.telegram.proxy.config}")
    private String telegramProxyConfig;

    @Value("${openai.url}")
    private String chatGptUrl;

    @Value("${openai.voice-to-text.url}")
    private String voiceToTextUrl;

    @Value("${replicate.url}")
    private String replicateUrl;

    @Value("${openai.model.temperature}")
    private float temperature;

    @Value("${replicate.apikey}")
    private String replicateKey;

    @Value("${openai.apikey}")
    private String openaiKey;

    @Value("${telegram.file.id.api}")
    private String fileIdApi;

    @Value("${telegram.file.path.api}")
    private String filePathApi;

    @Value("${telegram.photo.send.api}")
    private String photoSendApi;

    @Value("${telegram.chat.action.typing}")
    private String chatActionApi;

    @Value("${spring.security.user.name}")
    private String uiDefaultUser;

    @Value("${replicate.max-length}")
    private Integer voiceMaxLength;

    @Value("${replicate.max-wait}")
    private Long replicateMaxWait;

    @Value("${spring.security.user.password}")
    private String uiDefaultPassword;

    @Value("${spring.redis.messages.ttl}")
    private int messagesTtl;

    @Value("${telegram.chat.period}")
    private long chatPeriod;

    @Value("${telegram.user.balance}")
    private long defaultBalance;

    public static String CHAT_PERIOD_PER = "chatPer";

    public static String REPLY_MESSAGE_ID = "MessageId";

    public static String ROUTE_SELECT = "route";

    public static String BUTTON_RESPONSE = "buttonResponse";

    public static String USER_PROFILE = "userProfile";

    public static String LAST_MASSAGES = "lastMessages";

    public static String BODY_MESSAGE = "bodyMessage";

    public static String FILE_ID = "fileId";

    public static String FILE_PATH = "filePath";

    public static String BALANCE_MESSAGE = "balanceMessage";

    public static String ACTIVE_CHAT = "activeChat";

    public static String PHOTO_PATH = "photoPath";

    public String getAdminChatId() {
        return adminId;
    }

    public String getTelegramUri() {
        return "telegram:bots" + (telegramProxyEnable ? telegramProxyConfig : "");
    }

    public String getChatGptUrl() {
        return chatGptUrl;
    }

    public float getTemperature() {
        return temperature;
    }

    public long getChatPeriod() {
        return chatPeriod;
    }

    public String getOpenaiKey() {
        return openaiKey;
    }

    public Integer getVoiceMaxLength() {
        return voiceMaxLength;
    }

    public Long getReplicateMaxWait() {
        return replicateMaxWait;
    }

    public String getReplicateUrl() {
        return this.replicateUrl;
    }

    public String getReplicateKey() {
        return this.replicateKey;
    }

    public int getMessagesTtl() {
        return messagesTtl;
    }

    public String getFileIdApi(final String fileId) {
        return fileIdApi + fileId;
    }

    public String getFilePathApi(final String filePath) {
        return filePathApi + filePath;
    }

    public String getPhotoSendApi() {
        return photoSendApi;
    }

    public String getChatActionApi() {
        return chatActionApi;
    }

    public String getUiDefaultUser() {
        return uiDefaultUser;
    }

    public String getUiDefaultPassword() {
        return uiDefaultPassword;
    }

    public String getVoiceToTextUrl() {
        return voiceToTextUrl;
    }

    public long getDefaultBalance() {
        return defaultBalance;
    }
}
