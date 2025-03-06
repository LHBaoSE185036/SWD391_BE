package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.request.LoginRequest;
import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/v1/auth")
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        String token = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (token != null) {
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                            .errorCode(null)
                            .message("success")
                            .data(token)
                            .success(true)
                    .build());
        }
        return ResponseEntity.ok().body(ApiResponse.<String>builder()
                .errorCode(401)
                .message("fail")
                .data("Invalid username or password")
                .success(false)
                .build());
    }

}
