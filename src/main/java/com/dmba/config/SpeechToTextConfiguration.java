package com.dmba.config;

import com.dmba.controller.SseController;
import com.dmba.file.service.FileFinder;
import com.dmba.file.service.FileFinderImpl;
import com.dmba.file.watcher.FileWatcher;
import com.dmba.file.watcher.FileWatcherImpl;
import com.dmba.speech.SpeechToText;
import com.dmba.speech.SpeechToTextImpl;
import com.dmba.vosk.SpeechToTextModel;
import com.dmba.vosk.VoskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeechToTextConfiguration {

    @Bean
    public SpeechToTextModel getSpeechToTextModel(@Autowired SpeechProperties speechProperties) {
        return new VoskService(speechProperties);
    }

    @Bean
    public SpeechToText getSpeechToText(@Autowired SpeechProperties speechProperties,
                                        @Autowired SpeechToTextModel speechToTextModel,
                                        @Autowired SseController sseController) {
        return new SpeechToTextImpl(speechProperties, speechToTextModel, sseController);
    }
}
