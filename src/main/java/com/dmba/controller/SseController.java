package com.dmba.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter(1000000L); // 1000 sec
        this.emitters.add(emitter);
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        return emitter;
    }

    @SneakyThrows
    public void sendEvent(String message) {
        ObjectMapper mapper = new ObjectMapper();
        String formattedJson = message;
        Object obj = mapper.readValue(formattedJson, Object.class);
        String compactJson = mapper.writeValueAsString(obj);
        System.out.println("Sending message: " + compactJson);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("message").data(compactJson));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}