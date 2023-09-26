package com.dmba.config;

import com.dmba.file.service.FileFinder;
import com.dmba.file.service.FileFinderImpl;
import com.dmba.file.watcher.FileWatcher;
import com.dmba.file.watcher.FileWatcherImpl;
import com.dmba.speech.SpeechToText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileWatcherConfiguration {
    @Bean
    public FileFinder getFileFinder(@Autowired SpeechProperties speechProperties) {
        return new FileFinderImpl(speechProperties);
    }

    @Bean
    public FileWatcher getFileWatcher(@Autowired SpeechProperties speechProperties,
                                      @Autowired FileFinder fileFinder,
                                      @Autowired SpeechToText speechToText) {
        return new FileWatcherImpl(speechProperties,fileFinder, speechToText);
    }
}
