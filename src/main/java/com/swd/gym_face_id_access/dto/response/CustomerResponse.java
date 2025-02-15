package com.swd.gym_face_id_access.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class    CustomerResponse {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String status;
}
