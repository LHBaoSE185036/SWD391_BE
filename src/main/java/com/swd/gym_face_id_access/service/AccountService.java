package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CreateAccountRequest;
import com.swd.gym_face_id_access.model.Account;

public interface AccountService {

    String Login(String userName, String password);

    String Register(CreateAccountRequest createAccountRequest);

}
