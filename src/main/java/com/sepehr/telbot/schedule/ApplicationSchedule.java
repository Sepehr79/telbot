package com.sepehr.telbot.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ApplicationSchedule {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 7)
    public void deleteChatsWhenNotActiveFor1Month() {
        long currentTime = System.currentTimeMillis();
        jdbcTemplate.execute("DELETE FROM Active_Chat where current_time - " + currentTime  + " > 1000 * 60 * 60 * 24 * 30");
    }

}
