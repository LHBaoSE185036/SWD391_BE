package com.swd.gym_face_id_access.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
