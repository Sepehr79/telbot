package com.sepehr.telbot.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Active_Chat")
public class ActiveChat {

    @Id
    private String chatId;

    @Column(name="`last_update`")
    private long currentTime;

}
