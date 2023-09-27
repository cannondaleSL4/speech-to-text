package com.dmba;

import com.dmba.speech.SpeechToText;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class SpeechRecognitionTest {

    public static final String PATH = "./src/test/resources/sounds";

    private Set<String> expectedHashSet = new HashSet<>();


    @Autowired
    private SpeechToText speechToText;


    @BeforeEach
    private void setUp() {
        expectedHashSet.add(
                "{\n" +
                "  \"text\" : \"moscow is the capital of russian federation\"\n" +
                "}");
        expectedHashSet.add(
                "{\n" +
                "  \"text\" : \"washington d c is the capital of the united states\"\n" +
                "}");
        expectedHashSet.add(
                "{\n" +
                "  \"text\" : \"washington dc is the capital of the united states\"\n" +
                "}");
    }

    @Test
    public void speechToText() {

        Set<String> actualHashSet = new HashSet<>();

        File directory = new File(PATH);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".wav")) {
                    actualHashSet.add(speechToText.getTextFromSpeech(file));
                }
            }
        }

        assertTrue(expectedHashSet.containsAll(actualHashSet));
    }
}
