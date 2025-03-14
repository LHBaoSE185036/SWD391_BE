package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/v1/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping("/list-check-in-customer")
    public SseEmitter subscribeCheckInCustomerList() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseService.addEmitter(emitter);
        return emitter;
    }
}
