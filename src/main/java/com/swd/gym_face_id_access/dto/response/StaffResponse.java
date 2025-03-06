package com.swd.gym_face_id_access.dto.response;

import lombok.Data;

@Data
public class StaffResponse {
    private int staffId;
    private String staffName;
    private String role;
    private String email;
    private String account;
    private Boolean status;
}
