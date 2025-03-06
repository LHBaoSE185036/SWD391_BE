package com.swd.gym_face_id_access.dto.response;

import com.swd.gym_face_id_access.model.Enum.Roles;
import lombok.Data;

@Data
public class AccountResponse {
    private int accountId;
    private String userName;
    private Boolean status;
    private Roles role;
}
