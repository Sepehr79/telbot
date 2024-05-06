package com.sepehr.telbot.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Model {
    GPT35("gpt-3.5-turbo"),
    GPT4("gpt-4")
    ;
    private final String modelName;
}
