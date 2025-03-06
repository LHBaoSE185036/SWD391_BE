package com.swd.gym_face_id_access.dto.response;

import com.swd.gym_face_id_access.model.Enum.Roles;
import lombok.Data;

@Data
public class AccountDetailResponse {
    private String userName;
    private String password;
    private Boolean status;
    private Roles role;
    private String staffName;
}
