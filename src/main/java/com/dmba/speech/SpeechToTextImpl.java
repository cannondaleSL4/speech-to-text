package com.dmba.speech;

import com.dmba.controller.SseController;
import com.dmba.config.SpeechProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SpeechToTextImpl implements SpeechToText {

    @Autowired
    private SseController sseController;

    private List<SpeechRecognitionListener> listeners = new ArrayList<>();

    public void addListener(SpeechRecognitionListener listener) {
        listeners.add(listener);
    }

    private final SpeechProperties speechProperties;

    private final String dirModel;

    private Model model;

    @Autowired
    public SpeechToTextImpl(SpeechProperties speechProperties) {
        this.speechProperties = speechProperties;
        this.dirModel =speechProperties.getModelPath();
        initModel();
    }

    private void initModel() {
        try {
            System.load("/Users/dmitriybalasn/vosk-api/src/libvosk.dylib");
            this.model = new Model(dirModel);
            System.out.println("test");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing the speech model", e);
        }
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        if (model != null) {
            model.close();
        }
    }

    public String getTextFromSpeech(File file) {
        String result;
        try (InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
             Recognizer recognizer = new Recognizer(model, this.speechProperties.getSampleRate().floatValue())) {

            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = ais.read(b)) >= 0) {
                if (recognizer.acceptWaveForm(b, nbytes)) {
                    log.debug(recognizer.getResult());
                } else {
                    log.debug(recognizer.getPartialResult());
                }
            }
            result = recognizer.getResult();

            sseController.sendEvent(result);
            log.info(result);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }

        return result == null ? "" : result;
    }
}
