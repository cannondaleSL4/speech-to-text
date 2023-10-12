package com.dmba.file.watcher;

import com.dmba.file.service.FileFinder;
import com.dmba.config.SpeechProperties;
import com.dmba.speech.SpeechToText;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Collections;

@Slf4j
public class FileWatcherImpl implements FileWatcher {

    private final SpeechProperties speechProperties;
    private final FileFinder fileFinderService;
    private final Path dir;
    private final SpeechToText speechToText;


    public FileWatcherImpl(SpeechProperties speechProperties, FileFinder fileFinderService, SpeechToText speechToText) {
        this.speechProperties = speechProperties;
        this.fileFinderService = fileFinderService;
        this.dir = Paths.get(speechProperties.getDirPath());
        this.speechToText = speechToText;
    }

    @PostConstruct
    public void init() {
        new Thread(this::watch).start();
    }

    public void watch() {
        //todo
        //remove after debug
        log.info("The path of dir: {}", dir.toString());
        FileAlterationObserver observer = new FileAlterationObserver(dir.toString());
        observer.addListener(new FileAlterationListenerAdaptor() {

            @Override
            public void onFileCreate(File file) {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    Instant creationTime = attrs.creationTime().toInstant();
                    long sizeInKB = file.length() / 1024;
                    log.info("New file created: " + file.getName() + ", Creation time: " + creationTime + ", Size: " + sizeInKB + " KB");
                    speechToText.getTextFromSpeech(file);
                } catch (IOException e) {
                    log.error("Error reading file attributes for: " + file.getName(), e);
                }
            }

            @Override
            public void onFileChange(File file) {
            }

            @Override
            public void onFileDelete(File file) {
            }
        });

        FileAlterationMonitor monitor = new FileAlterationMonitor(1000, Collections.singleton(observer)); // проверка каждую секунду
        try {
            monitor.start();
            log.info("Watching directory: " + dir);
        } catch (Exception e) {
            log.error("Error while watching files", e);
        }
    }

}