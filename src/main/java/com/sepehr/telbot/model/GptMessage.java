package com.sepehr.telbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GptMessage {

    private final String role;

    private final String content;

}
