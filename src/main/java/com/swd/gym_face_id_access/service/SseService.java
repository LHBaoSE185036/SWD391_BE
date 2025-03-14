package com.swd.gym_face_id_access.service;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseService {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<SseEmitter>();

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
    }

    public void sendEvent(Object data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().data(data));
            } catch (Exception e) {
                deadEmitters.add(emitter); //Detect error emitter to delete
            }
        });
        emitters.removeAll(deadEmitters); // Delete  emitter no longer work
    }
}
