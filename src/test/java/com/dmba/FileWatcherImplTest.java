package com.dmba;

import com.dmba.config.FileWatcherConfiguration;
import com.dmba.config.FileWatcherTestConfig;
import com.dmba.config.SpeechProperties;
import com.dmba.file.service.FileFinder;
import com.dmba.file.watcher.FileWatcher;
import com.dmba.speech.SpeechToText;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {FileWatcherConfiguration.class, FileWatcherTestConfig.class})
@EnableConfigurationProperties(SpeechProperties.class)
public class FileWatcherImplTest {

    public static final String PATH = "./src/test/resources/sounds";

    @MockBean
    private SpeechToText speechToText;

    @Autowired
    private FileWatcher fileWatcher;

    @Autowired
    private SpeechProperties speechProperties;

    @AfterAll
    public static void clearUp() {
        File directory = new File(PATH);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".wav") && file.getName().contains("test")) {
                    log.info("Test temp file was cleared: " + file.getName());
                    file.deleteOnExit();
                }
            }
        }
    }

    @SneakyThrows
    @Test
    public void testWatch() {
        recreateWavFiles(PATH);
        Thread.sleep(3000);
        Mockito.verify(speechToText, Mockito.times(2)).getTextFromSpeech(Mockito.any(File.class));
    }

    private void recreateWavFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".wav")) {
                    String newFileName;

                    if (file.getName().startsWith("new_")) {
                        newFileName = file.getName().substring(4);
                    } else {
                        newFileName = "new_" + file.getName();
                    }

                    File newFile = new File(directory, newFileName);
                    try {
                        Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        if (file.delete()) {
                            System.out.println("Original file deleted: " + file.getName());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}