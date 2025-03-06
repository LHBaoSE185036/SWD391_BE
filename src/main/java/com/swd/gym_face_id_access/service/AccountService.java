package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.dto.request.CreateAccountRequest;
import com.swd.gym_face_id_access.dto.response.AccountDetailResponse;
import com.swd.gym_face_id_access.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {

    String login(String userName, String password);

    String register(CreateAccountRequest createAccountRequest);

    String deleteAccount(int accountId);

    AccountDetailResponse getAccount(int accountId);

    List<AccountResponse> getAllAccounts();
}
