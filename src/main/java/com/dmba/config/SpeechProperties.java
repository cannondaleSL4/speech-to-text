package com.dmba.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "speech")
@Configuration
@Getter
@Setter
public class SpeechProperties {
    private String dirPath;

    private String modelPath;

    private SampleRate sampleRate;

    public void setSampleRate(int value) {
        this.sampleRate = SampleRate.fromValue(value);
    }
}