package com.swd.gym_face_id_access.dto.request;

import lombok.Data;

@Data
public class UpdateCustomerRequest {
    private String fullName;
    private String phoneNumber;
    private String email;
}
