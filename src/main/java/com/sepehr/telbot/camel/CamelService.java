package com.sepehr.telbot.camel;

import com.sepehr.telbot.model.AdminMessageModel;
import com.sepehr.telbot.model.AppIncomingReq;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CamelService {

    private final ProducerTemplate producerTemplate;

    public void sendAdminMessage(final AdminMessageModel adminMessageModel) {
        final var appIncomingReq = new AppIncomingReq();
        if (!adminMessageModel.getContent().equals(""))
            appIncomingReq.setPhotoUrl(adminMessageModel.getContent());
        appIncomingReq.setBody(adminMessageModel.getCaption());
        producerTemplate.sendBodyAndHeaders("direct:ad", appIncomingReq, new HashMap<>());
    }

}
