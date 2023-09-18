package com.dmba.speech;

import com.dmba.controller.SseController;
import com.dmba.config.SpeechProperties;
import com.dmba.vosk.SpeechToTextModel;
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
public class SpeechToTextImpl implements SpeechToText {


    private SpeechToTextModel speechToTextModel;

    private List<SpeechRecognitionListener> listeners = new ArrayList<>();

    private final SpeechProperties speechProperties;

    private SseController sseController;

    public SpeechToTextImpl(SpeechProperties speechProperties,
                            SpeechToTextModel speechToTextModel,
                            SseController sseController) {
        this.sseController = sseController;
        this.speechProperties = speechProperties;
        this.speechToTextModel = speechToTextModel;
    }

//    public void addListener(SpeechRecognitionListener listener) {
//        listeners.add(listener);
//    }


    @Override
    public String getTextFromSpeech(File file) {
        String result =  speechToTextModel.getTextFromSpeech(file);
        sseController.sendEvent(result);
        return result;
    }
}
