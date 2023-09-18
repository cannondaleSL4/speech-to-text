package com.dmba.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "speech")
@Component
@Getter
@Setter
public class SpeechProperties {
    private String dirPath;

    private String modelPath;

    private SampleRate sampleRate;

    private String arm64Specific;

    public void setSampleRate(int value) {
        this.sampleRate = SampleRate.fromValue(value);
    }
}