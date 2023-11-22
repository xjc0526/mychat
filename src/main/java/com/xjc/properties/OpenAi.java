package com.xjc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "openai")
public class OpenAi {
    private String model;
    private String prompt;
    private Integer temperature;
    private Integer max_tokens;
    private Boolean stream;
}
