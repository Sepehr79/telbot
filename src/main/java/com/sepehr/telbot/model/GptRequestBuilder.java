package com.sepehr.telbot.model;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.config.UserProperties;
import com.sepehr.telbot.model.entity.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class GptRequestBuilder {

    private final ApplicationConfiguration applicationConfiguration;

    private final UserProperties userProperties;

    public GptReq createGptReq(Model model) {
        List<GptMessage> gptMessages = new ArrayList<>();
        gptMessages.add(new GptMessage(
                "system", "You are a helpful assistant that speaks persian. " +
                "your name is Telegram assistant and your talking as a Telegram bot." +
                "remember that if it was necessary to use english word in your sentence, " +
                "don't put the english word in beginning of the line"));
        return new GptReq(userProperties.getModel().get(model).getName(), applicationConfiguration.getTemperature(), gptMessages);
    }

    public GptMessage createAssistantMessage(final String content) {
        return new GptMessage("assistant", content);
    }

    public GptMessage createUserMessage(final String content) {
        return new GptMessage("user", content);
    }

    public GptReq createExampleGptRequest() {
        var gptReq = createGptReq(Model.GPT35);
        gptReq.getMessages().add(createUserMessage("My name is Sepehr"));
        gptReq.getMessages().add(createAssistantMessage("Ok, Ill remember that."));
        gptReq.getMessages().add(createUserMessage("Whats my name?"));
        return gptReq;
    }

}
