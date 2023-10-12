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

    public static void printDirectoryContents(String directoryPath) {
        File directory = new File(directoryPath);
        log.info("Checking the path: {}", directoryPath);

        if (directory.isDirectory()) {
            log.info("The path is a directory.");
            File[] contents = directory.listFiles();

            if (contents != null && contents.length > 0) {
                log.info("Listing contents:");
                for (File item : contents) {
                    if(item.isDirectory()) {
                        log.info("Directory: {}", item.getName());
                    } else {
                        log.info("File: {} (Size: {} bytes)", item.getName(), item.length());
                    }
                }
            } else {
                log.info("The directory is empty or there was an error reading its contents.");
            }
        } else if(directory.isFile()){
            log.info("The path points to a file: {} (Size: {} bytes)", directory.getName(), directory.length());
        } else {
            log.info("The provided path is neither a directory nor a file.");
        }
    }

    private void initModel() {
        try {
            //todo
            // remove after debug
            log.info("the files are: ");
            printDirectoryContents(speechProperties.getDirPath());
            printDirectoryContents(speechProperties.getModelPath());
            log.info("SPEECH_DIR_PATH: {}", System.getenv("SPEECH_DIR_PATH"));
            log.info("SPEECH_MODEL_PATH: {}", System.getenv("SPEECH_MODEL_PATH"));
            log.info("SpeechProperties: {}", speechProperties.getDirPath());
            log.info("SpeechProperties: {}", speechProperties.getModelPath());
            log.info("The dir path is: {}", dirModel);
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
