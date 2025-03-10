package com.swd.gym_face_id_access.dto.response;

import lombok.Data;

@Data
public class CheckInResponse {
    private String checkInResult;
    private String message;
    private Boolean isSuccess;
}
