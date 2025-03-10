package com.swd.gym_face_id_access.dto.response;

import lombok.Data;

@Data
public class FaceRecognitionResponse {
    private int customerId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String status;
}
