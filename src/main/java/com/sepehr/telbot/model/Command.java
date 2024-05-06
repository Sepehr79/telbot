package com.sepehr.telbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Command {

    START(0),
    DONATE(0),
    VERSION(0),
    CHAT(1000),
    VOICE(700),
    BALANCE(0)

    ;

    private final long usingBalance;


}
