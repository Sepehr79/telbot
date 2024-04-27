package com.sepehr.telbot.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AdminMessageModel {

    private String caption;

    private String content;

}
