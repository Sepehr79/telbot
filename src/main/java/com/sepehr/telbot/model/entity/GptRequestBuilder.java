package com.sepehr.telbot.model.entity;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class GptRequestBuilder {

    public GptReq createGptReq() {
        List<GptMessage> gptMessages = new ArrayList<>();
        gptMessages.add(new GptMessage("system", "You are a helpful assistant."));
        return new GptReq("gpt-3.5-turbo", gptMessages);
    }

    public GptMessage createAssistantMessage(final String content) {
        return new GptMessage("assistant", content);
    }

    public GptMessage createUserMessage(final String content) {
        return new GptMessage("user", content);
    }

    public GptReq createExampleGptRequest() {
        var gptReq = createGptReq();
        gptReq.getMessages().add(createUserMessage("My name is Sepehr"));
        gptReq.getMessages().add(createAssistantMessage("Ok, Ill remember that."));
        gptReq.getMessages().add(createUserMessage("Whats my name?"));
        return gptReq;
    }

}
