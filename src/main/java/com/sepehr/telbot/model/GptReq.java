package com.sepehr.telbot.model;



import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class GptReq {

    private final String model;

    private final List<GptMessage> messages;

}
