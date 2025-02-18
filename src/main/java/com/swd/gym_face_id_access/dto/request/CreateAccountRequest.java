package com.swd.gym_face_id_access.dto.request;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String username;
    private String password;
    private String Role;
}
