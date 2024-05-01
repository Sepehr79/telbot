package com.sepehr.telbot.service;

import com.sepehr.telbot.model.VoiceToTextModel;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Queue;

@Component
public class QueueService {

    private final Queue<VoiceToTextModel> voiceToTextModels = new ArrayDeque<>();

    public synchronized void appendVoiceToTextModel(VoiceToTextModel voiceToTextModel) {
        voiceToTextModels.add(voiceToTextModel);
    }

    public synchronized VoiceToTextModel peekVoiceToTextModel() {
        return voiceToTextModels.peek();
    }

    public synchronized VoiceToTextModel pollVoiceToTextModel() {
        return voiceToTextModels.poll();
    }

}
