package com.sepehr.telbot.config;

import com.sepehr.telbot.model.entity.Model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "user")
@Configuration
public class UserProperties {

    private final Map<Model, ModelProperties> model = new HashMap<>();


    public Map<Model, ModelProperties> getModel() {
        return model;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ModelProperties {

        private String name;

        private long chatCost;

        private long voiceCost;

        private long period;

    }
}
