package com.dmba.config;

import com.dmba.file.service.FileFinder;
import com.dmba.file.watcher.FileWatcher;
import com.dmba.file.watcher.FileWatcherImpl;
import com.dmba.speech.SpeechToText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class FileWatcherTestConfig {

    @Bean
    @Primary
    public FileWatcher getFileWatcher(@Autowired SpeechProperties speechProperties,
                                      @Autowired FileFinder fileFinder,
                                      @Autowired SpeechToText speechToText) {

        return new FileWatcherImpl(speechProperties,fileFinder, speechToText);
    }
}
