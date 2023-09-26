package com.dmba.vosk;

import java.io.File;

public interface SpeechToTextModel<T> {
    String getTextFromSpeech(T source);
}

