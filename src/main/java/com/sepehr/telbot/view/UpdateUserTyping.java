package com.sepehr.telbot.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateUserTyping {

    @JsonProperty("user_id")
    private Long userId;

    private String action;

    public UpdateUserTyping(Long userId) {
        this.userId = userId;
        action = "typing";
    }

}
