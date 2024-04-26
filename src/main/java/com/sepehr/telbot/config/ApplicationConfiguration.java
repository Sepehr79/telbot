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

    public static String REPLY_MESSAGE_ID = "MessageId";

    public static String ROUTE_SELECT = "route";

    public static String BUTTON_RESPONSE = "buttonResponse";

    public static String USER_PROFILE = "userProfile";

    public static String BODY_MESSAGE = "bodyMessage";

    public static String FILE_ID = "photoId";

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

    public String getOpenaiKey() {
        return openaiKey;
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

}
