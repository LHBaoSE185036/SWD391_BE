package com.swd.gym_face_id_access.dto.response;

import lombok.Data;

@Data
public class CustomerDetailResponse {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String status;
    private String faceURL;
}
