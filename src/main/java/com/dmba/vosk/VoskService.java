package com.dmba.vosk;

import com.dmba.config.SpeechProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

@Slf4j
public class VoskService implements SpeechToTextModel<File> {

    SpeechProperties speechProperties;

    private final String dirModel;

    private Model model;

    public VoskService(SpeechProperties speechProperties) {
        this.speechProperties = speechProperties;
        dirModel = speechProperties.getModelPath();
        initModel();
    }

    private void initModel() {
        try {
            this.model = new Model(dirModel);
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

    @Override
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
            log.info(result);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }

        return result == null ? "" : result;
    }
}
