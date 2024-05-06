package com.sepehr.telbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActiveChat {

    @Id
    private String chatId;

    private long lastUpdate;

    private long balance;

    @Enumerated(EnumType.STRING)
    private Model usingModel;
}
