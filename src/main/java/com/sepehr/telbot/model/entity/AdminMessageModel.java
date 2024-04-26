package com.sepehr.telbot.model.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AdminMessageModel {

    private String caption;

    private String content;

}
