package com.dmba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Start {
    public static void main(String[] args) {
        System.setProperty("java.library.path", "/Users/dmitriybalasn/vosk-api/src");
        SpringApplication.run(Start.class, args);
    }
}
