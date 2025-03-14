package com.swd.gym_face_id_access.dto.request;

import lombok.Data;

@Data
public class CreateStaffRequest {
    private String fullName;
    private String email;
    private String role;
    private int accountId;
}
