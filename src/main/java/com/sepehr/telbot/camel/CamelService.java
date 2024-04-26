package com.sepehr.telbot.camel;

import com.sepehr.telbot.config.ApplicationConfiguration;
import com.sepehr.telbot.model.entity.AdminMessageModel;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.telegram.TelegramConstants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CamelService {

    private final ProducerTemplate producerTemplate;

    public void sendAdminMessage(final AdminMessageModel adminMessageModel) {
        Map<String, Object> headers = new HashMap<>();
        if (!adminMessageModel.getContent().equals(""))
            headers.put(ApplicationConfiguration.FILE_ID, adminMessageModel.getContent());
        producerTemplate.sendBodyAndHeaders("direct:ad", adminMessageModel.getCaption(), headers);
    }

}
