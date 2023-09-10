package com.dmba.file.watcher;

import com.dmba.file.service.FileFinder;
import com.dmba.config.SpeechProperties;
import com.dmba.speech.SpeechToText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class FileWatcherImpl implements FileWatcher {

    private final SpeechProperties speechProperties;
    private final FileFinder fileFinderService;
    private final Path dir;
    private final SpeechToText speechToText;

    @Autowired
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
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException ex) {
                    log.error("Watcher service interrupted", ex);
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    List<File> newFiles = fileFinderService.getNewFiles();
                    newFiles.forEach(file -> {
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                            Instant creationTime = attrs.creationTime().toInstant();
                            long sizeInKB = file.length() / 1024;
                            log.info("New file created: " + file.getName() + ", Creation time: " + creationTime + ", Size: " + sizeInKB + " KB");
                            speechToText.getTextFromSpeech(file);
                        } catch (IOException e) {
                            log.error("Error reading file attributes for: " + file.getName(), e);
                        }
                    });
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException ex) {
            log.error("Error while watching files", ex);
        }
    }
}