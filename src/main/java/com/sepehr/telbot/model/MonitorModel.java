package com.sepehr.telbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonitorModel {

    private int countOfUsers;

    private List<String> messages;

}
